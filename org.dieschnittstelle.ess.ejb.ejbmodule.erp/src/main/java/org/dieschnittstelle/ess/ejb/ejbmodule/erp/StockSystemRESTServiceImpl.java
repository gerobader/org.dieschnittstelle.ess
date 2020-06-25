package org.dieschnittstelle.ess.ejb.ejbmodule.erp;

import org.dieschnittstelle.ess.ejb.ejbmodule.erp.crud.ProductCRUDRemote;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ws.rs.BadRequestException;
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

    @Override
    public List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId) {
        if (pointOfSaleId > 0) {
            return stockSystem.getProductsOnStock(pointOfSaleId);
        } else {
            return stockSystem.getAllProductsOnStock();
        }
    }

    @Override
    public int getUnitsOnStock(long productId, long pointOfSaleId) {
        if (productId > 0) {
            AbstractProduct p = productCRUD.readProduct(productId);
            if (pointOfSaleId > 0) {
                return stockSystem.getUnitsOnStock((IndividualisedProductItem) p, pointOfSaleId);
            }
            return stockSystem.getTotalUnitsOnStock((IndividualisedProductItem) p);
        } else {
            throw new BadRequestException("Product Id was not supplied");
        }
    }

    @Override
    public List<Long> getPointsOfSale(long productId) {
        AbstractProduct p = productCRUD.readProduct(productId);
        if (p instanceof IndividualisedProductItem) {
            return stockSystem.getPointsOfSale((IndividualisedProductItem) p);
        } else {
            throw new BadRequestException("productId " + productId + " does not refer to an IndividualizedProductItem");
        }
    }
}
