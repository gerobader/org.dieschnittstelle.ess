package org.dieschnittstelle.ess.ejb.ejbmodule.erp;

import org.dieschnittstelle.ess.ejb.ejbmodule.erp.crud.PointOfSaleCRUDLocal;
import org.dieschnittstelle.ess.ejb.ejbmodule.erp.crud.StockItemCRUDLocal;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.PointOfSale;
import org.dieschnittstelle.ess.entities.erp.StockItem;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ws.rs.BadRequestException;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class StockSystemSingleton implements StockSystemLocal {

    @EJB
    private StockItemCRUDLocal siCrud;

    @EJB
    private PointOfSaleCRUDLocal posCrud;

    @Override
    public void addToStock(IndividualisedProductItem product, long pointOfSaleId, int units) {
        System.out.println("addToStock(): siCrud: " + siCrud + " of class " + siCrud.getClass());
        System.out.println("addToStock(): posCrud: " + posCrud + " of class " + posCrud.getClass());
        PointOfSale pos = posCrud.readPointOfSale(pointOfSaleId);
        StockItem stockItem = siCrud.readStockItem(product, pos);
        if (stockItem == null) {
            stockItem = new StockItem(product, pos, units);
            siCrud.createStockItem(stockItem);
        } else {
            stockItem.setUnits(stockItem.getUnits() + units);
        }
    }

    @Override
    public void removeFromStock(IndividualisedProductItem product, long pointOfSaleId, int units) {
        PointOfSale pos = posCrud.readPointOfSale(pointOfSaleId);
        StockItem stockItem = siCrud.readStockItem(product, pos);
        if (units > stockItem.getUnits()) {
            throw new BadRequestException("Cannot remove more units than are in stock");
        }
        stockItem.setUnits(stockItem.getUnits() - units);
    }

    @Override
    public List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId) {
        PointOfSale pos = posCrud.readPointOfSale(pointOfSaleId);
        List<StockItem> stockItems = siCrud.readStockItemsForPointOfSale(pos);
        List<IndividualisedProductItem> productList = new ArrayList<>();
        for (StockItem si : stockItems) {
            if (!productList.contains(si.getProduct())) {
                productList.add(si.getProduct());
            }
        }
        return productList;
    }

    @Override
    public List<IndividualisedProductItem> getAllProductsOnStock() {
        List<PointOfSale> pointsOfSale = posCrud.readAllPointsOfSale();
        List<IndividualisedProductItem> productList = new ArrayList<>();
        for (PointOfSale pos : pointsOfSale) {
            List<StockItem> stockItems = siCrud.readStockItemsForPointOfSale(pos);
            for (StockItem si : stockItems) {
                if (!productList.contains(si.getProduct())) {
                    productList.add(si.getProduct());
                }
            }
        }
        return productList;
    }

    @Override
    public int getUnitsOnStock(IndividualisedProductItem product, long pointOfSaleId) {
        PointOfSale pos = posCrud.readPointOfSale(pointOfSaleId);
        StockItem stockItem = siCrud.readStockItem(product, pos);
        return stockItem.getUnits();
    }

    @Override
    public int getTotalUnitsOnStock(IndividualisedProductItem product) {
        List<StockItem> stockItems = siCrud.readStockItemsForProduct(product);
        return stockItems.stream().mapToInt(si -> si.getUnits()).sum();
    }

    @Override
    public List<Long> getPointsOfSale(IndividualisedProductItem product) {
        List<StockItem> stockItems = siCrud.readStockItemsForProduct(product);
        List<Long> poss = new ArrayList<>();
        for (StockItem si : stockItems) {
            poss.add(si.getPos().getId());
        }
        return poss;
    }

    @Override
    public List<StockItem> getCompleteStock() {
        throw new UnsupportedOperationException("getCompleteStock() is not supported");
    }
}
