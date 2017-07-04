package product.rest.application;

import product.service.model.Product;

/**
 * Created by ravikuwi on 7/3/2017.
 */
public class ProductPOJO {

    private long id;
    private String name;
    private String description;
    private int price;
    private String sku;
    private int ratings;

    public ProductPOJO(long id, String name, String description, int price, String sku, int ratings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.sku = sku;
        this.ratings = ratings;
    }


    public ProductPOJO(){

    }

    public ProductPOJO(Product product){
        this.id=product.getProductId();
        this.name=product.getName();
        this.description=product.getDescription();
        this.price=product.getPrice();
        this.sku=product.getSku();
        this.ratings=product.getRatings();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getRatings() {
        return ratings;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }

    @Override
    public String toString() {
        return "ProductPOJO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", sku='" + sku + '\'' +
                ", ratings=" + ratings +
                '}';
    }
}
