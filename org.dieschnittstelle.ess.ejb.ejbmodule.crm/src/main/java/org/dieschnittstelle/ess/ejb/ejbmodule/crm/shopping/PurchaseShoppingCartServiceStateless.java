package org.dieschnittstelle.ess.ejb.ejbmodule.crm.shopping;

import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.ejb.ejbmodule.crm.*;
import org.dieschnittstelle.ess.ejb.ejbmodule.crm.crud.CustomerCRUDLocal;
import org.dieschnittstelle.ess.ejb.ejbmodule.crm.crud.TouchpointCRUDLocal;
import org.dieschnittstelle.ess.ejb.ejbmodule.erp.StockSystemLocal;
import org.dieschnittstelle.ess.ejb.ejbmodule.erp.crud.ProductCRUDLocal;
import org.dieschnittstelle.ess.ejb.ejbmodule.erp.crud.ProductCRUDRemote;
import org.dieschnittstelle.ess.entities.crm.*;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.ProductBundle;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class PurchaseShoppingCartServiceStateless implements PurchaseShoppingCartService {

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(PurchaseShoppingCartServiceStateless.class);

    /*
     * the three beans that are used
     */
    private ShoppingCartRemote shoppingCart;

    @EJB
    private ShoppingCartServiceLocal shoppingCartServiceLocal;

    @EJB
    private CustomerTrackingLocal customerTracking;

    @EJB
    private CampaignTrackingLocal campaignTracking;

    @EJB
    private CustomerCRUDLocal customerCRUDLocal;

    @EJB
    private TouchpointCRUDLocal touchpointCRUDLocal;

    @EJB
    private ProductCRUDLocal productCRUDLocal;

    @EJB
    private StockSystemLocal stockSystemLocal;

    /**
     * the customer
     */
    private Customer customer;

    /**
     * the touchpoint
     */
    private AbstractTouchpoint touchpoint;
    /*
     * verify whether campaigns are still valid
     */
    //@Transactional(Transactional.TxType.MANDATORY)
    public void verifyCampaigns() throws ShoppingException {
        if (this.customer == null || this.touchpoint == null) {
            throw new RuntimeException("cannot verify campaigns! No touchpoint has been set!");
        }
        List<ShoppingCartItem> items = new ArrayList<>();
        items.addAll(this.shoppingCart.getItems());
        for (ShoppingCartItem item : this.shoppingCart.getItems()) {
            if (item.isCampaign()) {
                int availableCampaigns = this.campaignTracking.existsValidCampaignExecutionAtTouchpoint(
                        item.getErpProductId(), this.touchpoint);
                logger.info("got available campaigns for product " + item.getErpProductId() + ": "
                        + availableCampaigns);
                // we check whether we have sufficient campaign items available
                if (availableCampaigns < item.getUnits()) {
                    throw new ShoppingException("verifyCampaigns() failed for productBundle " + item
                            + " at touchpoint " + this.touchpoint + "! Need " + item.getUnits()
                            + " instances of campaign, but only got: " + availableCampaigns);
                }
            }
        }
    }

    //@Transactional(Transactional.TxType.REQUIRES_NEW)
    public void purchase() throws ShoppingException {
        logger.info("purchase()");

        if (this.customer == null || this.touchpoint == null) {
            throw new RuntimeException(
                    "cannot commit shopping session! Either customer or touchpoint has not been set: " + this.customer
                            + "/" + this.touchpoint);
        }

        verifyCampaigns();

        // remove the products from stock
        checkAndRemoveProductsFromStock();
        // then we add a new customer transaction for the current purchase
        List<ShoppingCartItem> products = new ArrayList<ShoppingCartItem>();
        // Hier gibt es bestimmt einen besseren weg
        for (ShoppingCartItem item : this.shoppingCart.getItems()) {
            try {
                ShoppingCartItem clonedItem = (ShoppingCartItem) item.clone();
                products.add(clonedItem);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        CustomerTransaction transaction = new CustomerTransaction(this.customer, this.touchpoint, products);
        transaction.setCompleted(true);
        customerTracking.createTransaction(transaction);
        logger.info("purchase(): done.\n");

        /*
        try {
            verifyCampaigns();

            // remove the products from stock
            checkAndRemoveProductsFromStock();
            // then we add a new customer transaction for the current purchase
            List<ShoppingCartItem> products = new ArrayList<ShoppingCartItem>();
            // Hier gibt es bestimmt einen besseren weg
            for (ShoppingCartItem item : this.shoppingCart.getItems()) {
                try {
                    ShoppingCartItem clonedItem = (ShoppingCartItem) item.clone();
                    products.add(clonedItem);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
            CustomerTransaction transaction = new CustomerTransaction(this.customer, this.touchpoint, products);
            transaction.setCompleted(true);
            customerTracking.createTransaction(transaction);
            logger.info("purchase(): done.\n");
        } catch (ShoppingException e) {
            logger.info("Transaction was not completed: " + e.getMessage() + " | " + e.getReason());
        }*/
    }

    /*
     * TODO PAT2: complete the method implementation in your server-side component for shopping / purchasing
     */
    //@Transactional(Transactional.TxType.MANDATORY)
    private void checkAndRemoveProductsFromStock() /*throws ShoppingException*/{
        logger.info("checkAndRemoveProductsFromStock");
        List<ShoppingCartItem> items = new ArrayList<>(this.shoppingCart.getItems());
        for (ShoppingCartItem item : this.shoppingCart.getItems()) {

            // TODO: ermitteln Sie das AbstractProduct für das gegebene ShoppingCartItem. Nutzen Sie dafür dessen erpProductId und die ProductCRUD EJB
            AbstractProduct product = productCRUDLocal.readProduct(item.getErpProductId());
            if (item.isCampaign()) {
                campaignTracking.purchaseCampaignAtTouchpoint(item.getErpProductId(), this.touchpoint, item.getUnits());
                // TODO: wenn Sie eine Kampagne haben, muessen Sie hier
                // 1) ueber die ProductBundle Objekte auf dem Campaign Objekt iterieren, und
                Campaign campaign = (Campaign) product;
                for (ProductBundle productBundle : campaign.getBundles()) {
                    // 2) fuer jedes ProductBundle das betreffende Produkt in der auf dem Bundle angegebenen Anzahl, multipliziert mit dem Wert von
                    // item.getUnits() aus dem Warenkorb,
                    AbstractProduct bundleProduct = productBundle.getProduct();
                    int productCount = item.getUnits() * productBundle.getUnits();
                    // - hinsichtlich Verfuegbarkeit ueberpruefen, und
                    // - falls verfuegbar, aus dem Warenlager entfernen - nutzen Sie dafür die StockSystem EJB
                    // (Anm.: item.getUnits() gibt Ihnen Auskunft darüber, wie oft ein Produkt, im vorliegenden Fall eine Kampagne, im
                    // Warenkorb liegt)
                    if (stockSystemLocal.getUnitsOnStock((IndividualisedProductItem) bundleProduct, this.touchpoint.getErpPointOfSaleId()) >= productCount) {
                        stockSystemLocal.removeFromStock((IndividualisedProductItem) bundleProduct, this.touchpoint.getErpPointOfSaleId(), productCount);
                    }/* else {
                        throw new ShoppingException(ShoppingException.ShoppingSessionExceptionReason.STOCK_EXCEEDED);
                    }*/
                }

            } else {
                // TODO: andernfalls (wenn keine Kampagne vorliegt) muessen Sie
                // 1) das Produkt in der in item.getUnits() angegebenen Anzahl hinsichtlich Verfuegbarkeit ueberpruefen und
                if (stockSystemLocal.getUnitsOnStock((IndividualisedProductItem) product, this.touchpoint.getErpPointOfSaleId()) >= item.getUnits()) {
                    // 2) das Produkt, falls verfuegbar, in der entsprechenden Anzahl aus dem Warenlager entfernen
                    stockSystemLocal.removeFromStock((IndividualisedProductItem) product, this.touchpoint.getErpPointOfSaleId(), item.getUnits());
                }/* else {
                    throw new ShoppingException(ShoppingException.ShoppingSessionExceptionReason.STOCK_EXCEEDED);
                }*/
            }

        }
    }


    @Override
    public void purchaseCartAtTouchpointForCustomer(long shoppingCartId, long touchpointId, long customerId) throws ShoppingException {
        this.shoppingCart = shoppingCartServiceLocal.getCartForId(shoppingCartId);
        this.touchpoint = touchpointCRUDLocal.readTouchpoint(touchpointId);
        this.customer = customerCRUDLocal.readCustomer(customerId);

        purchase();
    }
}
