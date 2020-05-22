package org.dieschnittstelle.ess.jrs;

import java.util.List;

import org.dieschnittstelle.ess.entities.GenericCRUDExecutor;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import static org.dieschnittstelle.ess.utils.Utils.show;

/*
 * TODO JRS2: implementieren Sie hier die im Interface deklarierten Methoden
 */

public class ProductCRUDServiceImpl implements IProductCRUDService {

	private GenericCRUDExecutor<AbstractProduct> productCRUD;

	public ProductCRUDServiceImpl(@Context ServletContext servletContext, @Context HttpServletRequest request) {
		this.productCRUD = (GenericCRUDExecutor<AbstractProduct>)servletContext.getAttribute("productCRUD");
	}

	@Override
	public AbstractProduct createProduct(
			AbstractProduct prod) {
		show("create Product: ", prod);
		return (AbstractProduct) this.productCRUD.createObject(prod);
	}

	@Override
	public List<AbstractProduct> readAllProducts() {
		return (List)this.productCRUD.readAllObjects();
	}

	@Override
	public AbstractProduct updateProduct(long id,
			AbstractProduct update) {
		// id is not needed for updateObject?
		return (AbstractProduct) this.productCRUD.updateObject(update);
	}

	@Override
	public boolean deleteProduct(long id) {
		return this.productCRUD.deleteObject(id);
	}

	@Override
	public AbstractProduct readProduct(long id) {
		return (AbstractProduct) this.productCRUD.readObject(id);
	}
	
}
