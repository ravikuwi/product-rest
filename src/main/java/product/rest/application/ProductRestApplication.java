package product.rest.application;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import product.service.model.Product;
import product.service.service.ProductLocalService;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static product.rest.application.ProductRestApplication.ProductsFromFile.importProducts;


@ApplicationPath("/products")
@Component(immediate = true, service = Application.class)
public class ProductRestApplication extends Application {

	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<Object>();
		singletons.add(new JacksonJsonProvider());
		singletons.add(this);

		return singletons;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProductPOJO> getAllProducts() {
		List<Product> products = _productLocalService.getProducts(-1, -1);
		List<ProductPOJO> productPojo = new ArrayList<>();


		//If there are no products in the DB, initialize with json file
		if (products == null || products.size()==0) {
			products = new ArrayList<>();
			try {
				List<ProductsFromFile> productsFromFile = importProducts();
				for(ProductsFromFile productFromFile:productsFromFile){
					Product product = _productLocalService.createProduct(_counterLocalService.increment(Product.class.getName()));
					product.setName(productFromFile.name);
					product.setDescription(productFromFile.description);
					product.setPrice(Integer.valueOf(productFromFile.price));
					product.setRatings(Integer.valueOf(productFromFile.ratings));
					product.setSku(productFromFile.sku);
					_productLocalService.addProduct(product);
					products.add(product);
				}

			} catch (IOException e) {
				_log.error("Error in reading products from json", e);
			}
		}

		products.forEach(product->productPojo.add(new ProductPOJO(product)));

		return productPojo;
	}


	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ProductPOJO getProduct(@PathParam("id") long productId){
		try {
			Product product = _productLocalService.getProduct(productId);
			if(product!=null){
				return new ProductPOJO(product);
			}
		} catch (PortalException e) {
			_log.error("Error in getting product with id"+productId,e);
		}

		return new ProductPOJO();
	}



	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ProductPOJO addProduct(ProductPOJO productPOJO){
        Product product = _productLocalService.createProduct(_counterLocalService.increment(Product.class.getName()));
        product.setName(productPOJO.getName());
        product.setDescription(productPOJO.getDescription());
        product.setRatings(productPOJO.getRatings());
        product.setSku(productPOJO.getSku());
        product.setPrice(productPOJO.getPrice());
        Product prod = _productLocalService.addProduct(product);
        return new ProductPOJO(prod);
	}


	@DELETE
	@Path("/{id}")
	public void deleteProduct(@PathParam("id") long id){
		try {
			_productLocalService.deleteProduct(id);
		} catch (PortalException e) {
			_log.error("Error in deleting the product",e);
		}

	}


	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ProductPOJO updateProduct(ProductPOJO productPOJO){
		Product product = null;
		try {
			_log.info("Received productPOJO"+productPOJO);
			product = _productLocalService.getProduct(productPOJO.getId());
			product.setName(productPOJO.getName());
			product.setDescription(productPOJO.getDescription());
			product.setRatings(productPOJO.getRatings());
			product.setSku(productPOJO.getSku());
			product.setPrice(productPOJO.getPrice());
			Product prod = _productLocalService.updateProduct(product);
			return new ProductPOJO(prod);
		} catch (PortalException e) {
			_log.error("Error in updating the product",e);
		}
		return new ProductPOJO();
	}


	@Reference
	private volatile ProductLocalService _productLocalService;

	@Reference
	private volatile CounterLocalService _counterLocalService;


	private static final Log _log = LogFactoryUtil.getLog(ProductRestApplication.class);


	static class ProductsFromFile{
		private String id, name, description, price, sku, ratings;

		static List<ProductsFromFile> importProducts() throws IOException{
			return new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY).readValue(ProductsFromFile.class.getResourceAsStream("/products.db.json"),new TypeReference<List<ProductsFromFile>>(){});
		}
	}

}