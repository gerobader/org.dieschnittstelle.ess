package org.dieschnittstelle.ess.ejb.ejbmodule.erp.crud;

import org.dieschnittstelle.ess.entities.erp.AbstractProduct;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
@WebService(targetNamespace = "http://dieschnittstelle.org/ess/jws", serviceName = "ProductCRUDRemoteWebService", endpointInterface = "org.dieschnittstelle.ess.ejb.ejbmodule.erp.crud.ProductCRUDRemote")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class ProductCRUDStateless implements ProductCRUDRemote, ProductCRUDLocal{

    @PersistenceContext(unitName = "erp_PU")
    private EntityManager em;

    @Override
    public AbstractProduct createProduct(AbstractProduct prod) {
        em.persist(prod);
        return prod;
    }

    @Override
    public List<AbstractProduct> readAllProducts() {
        Query q = em.createQuery("SELECT e FROM AbstractProduct e");
        return q.getResultList();
    }

    @Override
    public AbstractProduct updateProduct(AbstractProduct update) {
        return em.merge(update);
    }

    @Override
    public AbstractProduct readProduct(long productID) {
        return em.find(AbstractProduct.class, productID);
    }

    @Override
    public boolean deleteProduct(long productID) {
        AbstractProduct p = em.find(AbstractProduct.class, productID);
        if (p != null) {
            em.remove(p);
            return true;
        }
        return false;
    }
}
