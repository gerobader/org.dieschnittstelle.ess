package org.dieschnittstelle.ess.ejb.ejbmodule.erp;

import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * TODO JPA3/4/6:
 * - declare the web api for this interface using JAX-RS
 * - implement the interface as an EJB of an appropriate type
 * - in the EJB implementation, delegate method invocations to the corresponding methods of the StockSystem EJB via the local interface
 * - let the StockSystemClient in the client project access the web api via this interface - see ShoppingCartClient for an example
 */
@Path("/stocksystem")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public interface StockSystemRESTService {

	/**
	 * adds some units of a product to the stock of a point of sale
	 */
	@POST
    void addToStock(@QueryParam("productId") long productId, @QueryParam("pointOfSaleId") long pointOfSaleId, @QueryParam("units") int units);

	/**
	 * removes some units of a product from the stock of a point of sale
	 */
	@DELETE
	void removeFromStock(@QueryParam("productId") long productId, @QueryParam("pointOfSaleId") long pointOfSaleId, @QueryParam("units") int units);

	/**
	 * returns all products on stock of some pointOfSale
	 */
//    List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId);
//
//	/**
//	 * returns all products on stock
//	 */
//    List<IndividualisedProductItem> getAllProductsOnStock();
//
//	/**
//	 * returns the units on stock for a product at some point of sale
//	 */
//    int getUnitsOnStock(long productId, long pointOfSaleId);
//
//	/**
//	 * returns the total number of units on stock for some product
//	 */
//    int getTotalUnitsOnStock(long productId);
//
//	/**
//	 * returns the points of sale where some product is available
//	 */
//    List<Long> getPointsOfSale(long productId);

}
