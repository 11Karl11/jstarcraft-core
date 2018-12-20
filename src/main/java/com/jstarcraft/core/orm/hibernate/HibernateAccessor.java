package com.jstarcraft.core.orm.hibernate;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import com.jstarcraft.core.cache.CacheObject;
import com.jstarcraft.core.orm.OrmAccessor;
import com.jstarcraft.core.orm.OrmIterator;
import com.jstarcraft.core.orm.OrmMetadata;
import com.jstarcraft.core.orm.OrmPagination;
import com.jstarcraft.core.orm.exception.OrmException;
import com.jstarcraft.core.orm.exception.OrmQueryException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Hibernate访问器
 * 
 * @author Birdy
 */
@Transactional
public class HibernateAccessor extends HibernateDaoSupport implements OrmAccessor {

	private static final int BATCH_SIZE = 1000;

	private enum Operation {
		AND, OR;
	}

	// 内置查询
	/** DELETE Class clazz WHERE clazz.field = ? */
	private final static String DELETE_HQL = "DELETE {} clazz WHERE clazz.{} = ?0";

	/** 查询指定范围的最大主键标识 */
	private final static String MAXIMUM_ID = "SELECT MAX(clazz.{}) FROM {} clazz WHERE clazz.{} BETWEEN ?0 AND ?1";

	/** 查询指定范围的最小主键标识 */
	private final static String MINIMUM_ID = "SELECT MIN(clazz.{}) FROM {} clazz WHERE clazz.{} BETWEEN ?0 AND ?1";

	/** 查询指定索引范围的主键映射 */
	private final static String INDEX_2_ID_MAP = "SELECT clazz.{}, clazz.{} FROM {} clazz";

	/** 查询指定索引范围的对象列表 */
	private final static String INDEX_2_OBJECT_SET = "FROM {} clazz";

	private final static String BETWEEN_CONDITION = " WHERE clazz.{} BETWEEN ?0 AND ?1";

	private final static String EQUAL_CONDITION = " WHERE clazz.{} = ?0";

	private final static String IN_CONDITION = " WHERE clazz.{} IN (?0{})";

	/** HQL删除语句 */
	private Map<String, String> deleteHqls = new ConcurrentHashMap<>();

	/** HQL查询语句(查询指定范围的最大主键标识),用于IdentityManager */
	private Map<String, String> maximumIdHqls = new ConcurrentHashMap<>();

	/** HQL查询语句(查询指定范围的最小主键标识),用于IdentityManager */
	private Map<String, String> minimumIdHqls = new ConcurrentHashMap<>();

	/** Hibernate元信息 */
	protected Map<String, HibernateMetadata> hibernateMetadatas = new ConcurrentHashMap<>();

	public HibernateAccessor(EntityManagerFactory sessionFactory) {
		MetamodelImplementor metamodelImplementor = (MetamodelImplementor) sessionFactory.getMetamodel();
		try {
			for (EntityPersister ormPersister : metamodelImplementor.entityPersisters().values()) {
				ClassMetadata classMetadata = ormPersister.getClassMetadata();
				String ormName = classMetadata.getEntityName();
				try {
					Class<?> ormClass = Class.forName(ormName);
					HibernateMetadata hibernateMetadata = new HibernateMetadata(ormClass);
					hibernateMetadatas.put(ormName, hibernateMetadata);
					String deleteHql = StringUtility.format(DELETE_HQL, ormClass.getSimpleName(), hibernateMetadata.getPrimaryName());
					deleteHqls.put(ormName, deleteHql);

					String maximumIdHql = StringUtility.format(MAXIMUM_ID, hibernateMetadata.getPrimaryName(), ormClass.getSimpleName(), hibernateMetadata.getPrimaryName());
					maximumIdHqls.put(ormName, maximumIdHql);

					String minimumIdHql = StringUtility.format(MINIMUM_ID, hibernateMetadata.getPrimaryName(), ormClass.getSimpleName(), hibernateMetadata.getPrimaryName());
					minimumIdHqls.put(ormName, minimumIdHql);
				} catch (ClassNotFoundException exception) {
					throw new OrmException(exception);
				}
			}
		} catch (Exception exception) {
			throw new OrmException(exception);
		}
		setSessionFactory((SessionFactory) sessionFactory);
	}

	@Override
	public Collection<? extends OrmMetadata> getAllMetadata() {
		return hibernateMetadatas.values();
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> T get(Class<T> clazz, K id) {
		T value = getHibernateTemplate().executeWithNativeSession(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException {
				return (T) session.get(clazz, (Serializable) id);
			}

		});
		return value;
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> K create(Class<T> clazz, T object) {
		K value = getHibernateTemplate().executeWithNativeSession(new HibernateCallback<K>() {

			@Override
			public K doInHibernate(Session session) throws HibernateException {
				return (K) session.save(object);
			}

		});
		return value;
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> void delete(Class<T> clazz, K id) {
		getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Void>() {

			@Override
			public Void doInHibernate(Session session) throws HibernateException {
				String hql = deleteHqls.get(clazz.getName());
				Query<?> query = session.createQuery(hql);
				query.setParameter(0, id);
				query.executeUpdate();
				return null;
			}

		});
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> void delete(Class<T> clazz, T object) {
		getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Void>() {

			@Override
			public Void doInHibernate(Session session) throws HibernateException {
				session.delete(object);
				return null;
			}

		});
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> void update(Class<T> clazz, T object) {
		getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Void>() {

			@Override
			public Void doInHibernate(Session session) throws HibernateException {
				session.update(object);
				return null;
			}

		});
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> K maximumIdentity(Class<T> clazz, K from, K to) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<K>() {

			@Override
			public K doInHibernate(Session session) throws HibernateException {
				String hql = maximumIdHqls.get(clazz.getName());
				Query<?> query = session.createQuery(hql);
				query.setParameter(0, from);
				query.setParameter(1, to);
				return (K) query.getSingleResult();
			}

		});
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> K minimumIdentity(Class<T> clazz, K from, K to) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<K>() {

			@Override
			public K doInHibernate(Session session) throws HibernateException {
				String hql = minimumIdHqls.get(clazz.getName());
				Query<?> query = session.createQuery(hql);
				query.setParameter(0, from);
				query.setParameter(1, to);
				return (K) query.getSingleResult();
			}

		});
	}

	@Override
	public <K extends Comparable, I, T extends CacheObject<K>> Map<K, I> queryIdentities(Class<T> clazz, String name, I... values) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Map<K, I>>() {

			@Override
			public Map<K, I> doInHibernate(Session session) throws HibernateException {
				StringBuilder buffer = new StringBuilder(INDEX_2_ID_MAP);
				if (values.length > 2) {
					StringBuilder string = new StringBuilder();
					for (int index = 1, size = values.length - 1; index <= size; index++) {
						string.append(", ?");
						string.append(index);
					}
					buffer.append(StringUtility.format(IN_CONDITION, name, string.toString()));
				} else if (values.length > 1) {
					buffer.append(BETWEEN_CONDITION);
				} else if (values.length > 0) {
					buffer.append(EQUAL_CONDITION);
				}
				String hql = buffer.toString();
				HibernateMetadata hibernateMetadata = hibernateMetadatas.get(clazz.getName());
				hql = StringUtility.format(hql, hibernateMetadata.getPrimaryName(), name, clazz.getSimpleName(), name);
				Query<Object[]> query = session.createQuery(hql);
				for (int index = 0; index < values.length; index++) {
					query.setParameter(index, values[index]);
				}
				List<Object[]> list = query.getResultList();
				Map<K, I> map = new HashMap<>();
				for (Object[] element : list) {
					map.put((K) element[0], (I) element[1]);
				}
				return map;
			}

		});
	}

	@Override
	public <K extends Comparable, I, T extends CacheObject<K>> List<T> queryInstances(Class<T> clazz, String name, I... values) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<T>>() {

			@Override
			public List<T> doInHibernate(Session session) throws HibernateException {
				StringBuilder buffer = new StringBuilder(INDEX_2_OBJECT_SET);
				if (values.length > 2) {
					StringBuilder string = new StringBuilder();
					for (int index = 1, size = values.length - 1; index <= size; index++) {
						string.append(", ?");
						string.append(index);
					}
					buffer.append(StringUtility.format(IN_CONDITION, name, string.toString()));
				} else if (values.length > 1) {
					buffer.append(BETWEEN_CONDITION);
				} else if (values.length > 0) {
					buffer.append(EQUAL_CONDITION);
				}
				String hql = buffer.toString();
				hql = StringUtility.format(hql, clazz.getSimpleName(), name);
				Query<T> query = session.createQuery(hql);
				for (int index = 0; index < values.length; index++) {
					query.setParameter(index, values[index]);
				}
				List<T> list = query.getResultList();
				return list;
			}
		});

	}

	public <K extends Comparable, I, T extends CacheObject<K>> List<T> query(Class<T> clazz, String name, I... values) {
		return null;
	}

	private <K extends Comparable, T extends CacheObject<K>> List<T> query(Class<T> clazz, Operation operation, Map<String, Object> condition, OrmPagination pagination) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<T>>() {

			@Override
			public List<T> doInHibernate(Session session) throws HibernateException {
				CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
				CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
				Root<T> root = criteriaQuery.from(clazz);
				if (condition != null) {
					Predicate left = null, right = null;
					final Iterator<Entry<String, Object>> iterator = condition.entrySet().iterator();
					if (iterator.hasNext()) {
						Entry<String, Object> entry = iterator.next();
						left = criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue());
					}
					while (iterator.hasNext()) {
						Entry<String, Object> entry = iterator.next();
						right = criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue());
						switch (operation) {
						case AND:
							left = criteriaBuilder.and(left, right);
							break;
						case OR:
							left = criteriaBuilder.or(left, right);
							break;
						default:
							throw new UnsupportedOperationException();
						}
					}
					if (left != null) {
						criteriaQuery.where(left);
					}
				}
				TypedQuery<T> typedQuery = session.createQuery(criteriaQuery);
				if (pagination != null) {
					typedQuery.setFirstResult(pagination.getFirst());
					typedQuery.setMaxResults(pagination.getSize());
				}
				List<T> value = typedQuery.getResultList();
				return value;
			}

		});
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> List<T> query(Class<T> clazz, OrmPagination pagination) {
		return query(clazz, null, null, pagination);
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> List<T> queryIntersection(Class<T> clazz, Map<String, Object> condition, OrmPagination pagination) {
		return query(clazz, Operation.AND, condition, pagination);
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> List<T> queryUnion(Class<T> clazz, Map<String, Object> condition, OrmPagination pagination) {
		return query(clazz, Operation.OR, condition, pagination);
	}

	private <K extends Comparable, T extends CacheObject<K>> long count(Class<T> clazz, Operation operation, Map<String, Object> condition) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Long>() {

			@Override
			public Long doInHibernate(Session session) throws HibernateException {
				CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
				CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
				Root<T> root = criteriaQuery.from(clazz);
				if (condition != null) {
					Predicate left = null, right = null;
					final Iterator<Entry<String, Object>> iterator = condition.entrySet().iterator();
					if (iterator.hasNext()) {
						Entry<String, Object> entry = iterator.next();
						left = criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue());
					}
					while (iterator.hasNext()) {
						Entry<String, Object> entry = iterator.next();
						right = criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue());
						switch (operation) {
						case AND:
							left = criteriaBuilder.and(left, right);
							break;
						case OR:
							left = criteriaBuilder.or(left, right);
							break;
						default:
							throw new UnsupportedOperationException();
						}
					}
					if (left != null) {
						criteriaQuery.where(left);
					}
				}
				criteriaQuery.select(criteriaBuilder.countDistinct(root));
				TypedQuery<Long> typedQuery = session.createQuery(criteriaQuery);
				long count = typedQuery.getSingleResult().longValue();
				return count;
			}

		});
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> long count(Class<T> clazz) {
		return count(clazz, null, null);
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> long countIntersection(Class<T> clazz, Map<String, Object> condition) {
		return count(clazz, Operation.AND, condition);
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> long countUnion(Class<T> clazz, Map<String, Object> condition) {
		return count(clazz, Operation.OR, condition);
	}

	private <K extends Comparable, T extends CacheObject<K>> void iterate(OrmIterator<T> iterator, final Class<T> clazz, Operation operation, Map<String, Object> condition, OrmPagination pagination) {
		getHibernateTemplate().executeWithNativeSession(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException {
				CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
				CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
				Root<T> root = criteriaQuery.from(clazz);
				if (condition != null) {
					Predicate left = null, right = null;
					final Iterator<Entry<String, Object>> cursor = condition.entrySet().iterator();
					if (cursor.hasNext()) {
						Entry<String, Object> entry = cursor.next();
						left = criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue());
					}
					while (cursor.hasNext()) {
						Entry<String, Object> entry = cursor.next();
						right = criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue());
						switch (operation) {
						case AND:
							left = criteriaBuilder.and(left, right);
							break;
						case OR:
							left = criteriaBuilder.or(left, right);
							break;
						default:
							throw new UnsupportedOperationException();
						}
					}
					if (left != null) {
						criteriaQuery.where(left);
					}
				}
				TypedQuery<T> typedQuery = session.createQuery(criteriaQuery);
				if (pagination != null) {
					typedQuery.setFirstResult(pagination.getFirst());
					typedQuery.setMaxResults(pagination.getSize());
				}
				// 设置遍历过程的参数
				Query<T> query = (Query<T>) typedQuery;
				query.setFetchSize(BATCH_SIZE);
				query.setLockMode(LockModeType.NONE);
				query.setReadOnly(true);
				try (ScrollableResults scrollableResults = query.scroll()) {
					while (scrollableResults.next()) {
						try {
							// TODO 需要考虑中断
							final T object = clazz.cast(scrollableResults.get(0));
							iterator.iterate(object);
						} catch (Throwable throwable) {
							throw new OrmQueryException(throwable);
						}
					}
				}
				return null;
			}

		});
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> void iterate(OrmIterator<T> iterator, final Class<T> clazz, OrmPagination pagination) {
		iterate(iterator, clazz, null, null, pagination);
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> void iterateIntersection(OrmIterator<T> iterator, final Class<T> clazz, Map<String, Object> condition, OrmPagination pagination) {
		iterate(iterator, clazz, Operation.AND, condition, pagination);
	}

	@Override
	public <K extends Comparable, T extends CacheObject<K>> void iterateUnion(OrmIterator<T> iterator, final Class<T> clazz, Map<String, Object> condition, OrmPagination pagination) {
		iterate(iterator, clazz, Operation.OR, condition, pagination);
	}

	public <R> List<R> query(String name, Class<R> queryType, OrmPagination pagination, Map<String, Object> parameters) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<R>>() {
			@Override
			public List<R> doInHibernate(Session session) throws HibernateException {
				javax.persistence.Query query;
				if (queryType == null) {
					// 执行更新与删除不可以指定类型
					query = session.createNamedQuery(name);
				} else {
					query = session.createNamedQuery(name, queryType);
				}
				for (Entry<String, Object> keyValue : parameters.entrySet()) {
					String name = keyValue.getKey();
					Object value = keyValue.getValue();
					query.setParameter(name, value);
				}
				if (pagination != null) {
					query.setFirstResult(pagination.getFirst());
					query.setMaxResults(pagination.getSize());
				}
				if (queryType == null) {
					// 执行更新与删除不可以指定类型
					Integer count = query.executeUpdate();
					return (List<R>) Arrays.asList(count);
				} else {
					List<R> value = query.getResultList();
					return value;
				}
			}
		});
	}

	public <R> List<R> query(final String name, Class<R> queryType, final OrmPagination pagination, final Object... parameters) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<R>>() {

			@Override
			public List<R> doInHibernate(Session session) throws HibernateException {
				javax.persistence.Query query;
				if (queryType == null) {
					// 执行更新与删除不可以指定类型
					query = session.createNamedQuery(name);
				} else {
					query = session.createNamedQuery(name, queryType);
				}
				for (int index = 0; index < parameters.length; index++) {
					query.setParameter(index, parameters[index]);
				}
				if (pagination != null) {
					query.setFirstResult(pagination.getFirst());
					query.setMaxResults(pagination.getSize());
				}
				if (queryType == null) {
					// 执行更新与删除不可以指定类型
					Integer count = query.executeUpdate();
					return (List<R>) Arrays.asList(count);
				} else {
					List<R> value = query.getResultList();
					return value;
				}
			}
		});

	}

	public <R> R unique(String name, Class<R> queryType, Map<String, Object> parameters) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<R>() {

			@Override
			public R doInHibernate(Session session) throws HibernateException {
				Query<?> query = session.createNamedQuery(name, queryType);
				for (Entry<String, Object> keyValue : parameters.entrySet()) {
					String name = keyValue.getKey();
					Object value = keyValue.getValue();
					query.setParameter(name, value);
				}
				R value = (R) query.getSingleResult();
				return value;
			}

		});
	}

	public <R> R unique(final String name, Class<R> queryType, final Object... parameters) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<R>() {

			@Override
			public R doInHibernate(Session session) throws HibernateException {
				Query<?> query = session.createNamedQuery(name, queryType);
				for (int index = 0; index < parameters.length; index++) {
					query.setParameter(index, parameters[index]);
				}
				R value = (R) query.getSingleResult();
				return value;
			}

		});
	}

	public int modify(final String name, Map<String, Object> condition) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Integer>() {

			@Override
			public Integer doInHibernate(Session session) throws HibernateException {
				Query<?> query = session.getNamedQuery(name);
				for (Entry<String, Object> keyValue : condition.entrySet()) {
					String name = keyValue.getKey();
					Object value = keyValue.getValue();
					query.setParameter(name, value);
				}
				return query.executeUpdate();
			}

		});
	}

	public int modify(final String name, final Object... parameters) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Integer>() {

			@Override
			public Integer doInHibernate(Session session) throws HibernateException {
				Query<?> query = session.getNamedQuery(name);
				for (int index = 0; index < parameters.length; index++) {
					query.setParameter(index, parameters[index]);
				}
				return query.executeUpdate();
			}

		});
	}

}
