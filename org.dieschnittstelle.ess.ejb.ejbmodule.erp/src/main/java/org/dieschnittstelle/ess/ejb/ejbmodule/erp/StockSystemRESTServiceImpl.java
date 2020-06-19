package org.dieschnittstelle.ess.ejb.ejbmodule.erp;

import org.dieschnittstelle.ess.ejb.ejbmodule.erp.crud.ProductCRUDRemote;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.util.List;

@Remote({StockSystemRESTService.class})
@Stateless
public class StockSystemRESTServiceImpl implements StockSystemRESTService{

    @EJB
    private StockSystemLocal stockSystem;

    @EJB
    private ProductCRUDRemote productCRUD;

    @Override
    public void addToStock(long productId, long pointOfSaleId, int units) {
        AbstractProduct prod = productCRUD.readProduct(productId);
        stockSystem.addToStock((IndividualisedProductItem) prod, pointOfSaleId, units);
    }

    @Override
    public void removeFromStock(long productId, long pointOfSaleId, int units) {
        AbstractProduct prod = productCRUD.readProduct(productId);
        stockSystem.removeFromStock((IndividualisedProductItem) prod, pointOfSaleId, units);
    }

//    @Override
//    public List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId) {
//        return null;
//    }
//
//    @Override
//    public List<IndividualisedProductItem> getAllProductsOnStock() {
//        return null;
//    }
//
//    @Override
//    public int getUnitsOnStock(long productId, long pointOfSaleId) {
//        return 0;
//    }
//
//    @Override
//    public int getTotalUnitsOnStock(long productId) {
//        return 0;
//    }
//
//    @Override
//    public List<Long> getPointsOfSale(long productId) {
//        return null;
//    }
}
