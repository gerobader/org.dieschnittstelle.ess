package org.dieschnittstelle.ess.ue.add2.junit;

import java.util.List;
import org.dieschnittstelle.ess.ejb.ejbmodule.erp.crud.ProductCRUDRemote;
import org.dieschnittstelle.ess.entities.erp.ws.AbstractProduct;
import org.dieschnittstelle.ess.jws.ProductCRUDRemoteWebService;
// TODO: generate classes given the wsdl
// TODO: remove the comments
// TODO: import all required classes from the generated packages

public class ProductCRUDClient {

	private ProductCRUDRemote proxy;

	public ProductCRUDClient() throws Exception {
		// TODO: instantiate the proxy using the generated web service classes
		ProductCRUDRemoteWebService service = new ProductCRUDRemoteWebService();
		proxy = service.getProductCRUDStatelessPort();
	}

	public AbstractProduct createProduct(AbstractProduct prod) {
		AbstractProduct created = proxy.createProduct(prod);
		// as a side-effect we set the id of the created product on the argument before returning
		prod.setId(created.getId());
		return created;
	}

	public List<AbstractProduct> readAllProducts() {
		return proxy.readAllProducts();
	}

	public AbstractProduct updateProduct(AbstractProduct update) {
		return proxy.updateProduct(update);
	}

	public AbstractProduct readProduct(long productID) {
		return proxy.readProduct(productID);
	}

	public boolean deleteProduct(long productID) {
		return proxy.deleteProduct(productID);
	}

}
