package com.jstarcraft.core.cache.proxy;

import com.jstarcraft.core.cache.CacheObject;

/**
 * 代理对象
 * 
 * <pre>
 * 所有缓存代理都需要实现此接口.
 * </pre>
 * 
 * @author Birdy
 */
public interface ProxyObject {

	/**
	 * 获取缓存对象
	 * 
	 * @return
	 */
	CacheObject getInstance();

}
