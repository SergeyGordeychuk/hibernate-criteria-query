package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone: " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> fromPhoneRoot = query.from(Phone.class);
            Predicate phonePredicate = cb.conjunction();
            for (Map.Entry<String,String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> predicate =
                        cb.in(fromPhoneRoot.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    predicate.value(value);
                }
                phonePredicate = cb.and(phonePredicate,predicate);
            }
            return session.createQuery(query.where(phonePredicate)).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all", e);
        }
    }
}
