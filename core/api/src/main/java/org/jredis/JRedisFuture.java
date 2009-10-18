/*
 *   Copyright 2009 Joubin Houshyar
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *    
 *   http://www.apache.org/licenses/LICENSE-2.0
 *    
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

/*
 *   Copyright 2009 Joubin Houshyar
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *    
 *   http://www.apache.org/licenses/LICENSE-2.0
 *    
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.jredis;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.jredis.protocol.ResponseStatus;


/**
 * The asynchronous interface to Redis.
 * <p>
 * This is effectively a one to one mapping to Redis commands.  Depending on the implementation
 * either the redis response and/or redis write are asynchronous.  Regardless, each method returns
 * an extension of {@link Future} and the returned results conforms to the contract of that interface (which
 * you should review).
 * <p>
 * If your request results in a {@link RedisException}, the call to {@link Future#get()} (of either flavor)
 * will raise a {@link ExecutionException} with {@link ExecutionException#getCause()} returning the underlying
 * {@link RedisException}.
 * <p>
 * Similarly, if the request results in either {@link ClientRuntimeException} or {@link ProviderException}, the
 * {@link Future}'s {@link ExecutionException} will wrap these as the cause.
 * <p>
 * Beyond that , just be aware that an implementation may throw {@link ClientRuntimeException}
 * or an extension to report problems (typically connectivity) or {@link ProviderException}
 * (to highlight implementation features/bugs).  
 * These are {@link RuntimeException} that have been encountered while trying to queue your request.
 * <p>
 * <b>Note</b> that this interface provides no guarantees whatsoever regarding the execution of your requests beyond
 * the strict ordering of the requests per your invocations.  Specifically, in the event of connection issues, this
 * interface's contract does not place any requirements on the implementation beyond to notify the user of such issues
 * either during a call to this interface, or, on the attempt to get the result of a pending response on {@link Future#get()}.
 * Refer to the documentation of the implementation of {@link JRedisFuture} for the specifics of behavior in context of
 * errors. 
 * 
 * @author  Joubin (alphazero@sensesay.net)
 * @version alpha.0, 04/02/09
 * @since   alpha.0
 * 
 */
public interface JRedisFuture {
	
	// ------------------------------------------------------------------------
	// "Connection Handling"
	// ------------------------------------------------------------------------

	/**
	 * Ping redis
	 */
	public Future<ResponseStatus> ping ();

	/**
	 * Disconnects the client.
	 * @Redis QUIT
	 */
	public Future<ResponseStatus> quit ();
	
	// ------------------------------------------------------------------------
	// "Commands operating on string values"
	// ------------------------------------------------------------------------

	/**
	 * Bind the value to key.  
	 * @Redis SET
	 * @param key any UTF-8 {@link String}
	 * @param value any bytes.  For current data size limitations, refer to
	 * Redis documentation.
	 * @throws ProviderException on un-documented features/bug
	 * @throws ClientRuntimeException on errors due to operating environment (Redis or network)
	 */
	public Future<ResponseStatus> set (String key, byte[] value);
	/**
	 * Convenient method for {@link String} data binding
	 * @Redis SET
	 * @param key
	 * @param stringValue
	 * @see {@link JRedis#set(String, byte[])}
	 */
	public Future<ResponseStatus> set (String key, String stringValue);
	/**
	 * Convenient method for {@link String} numeric values binding
	 * @Redis SET
	 * @param key
	 * @param numberValue
	 * @see {@link JRedis#set(String, byte[])}
	 */
	public Future<ResponseStatus> set (String key, Number numberValue);
	/**
	 * Binds the given java {@link Object} to the key.  Serialization format is
	 * implementation specific.  Simple implementations may apply the basic {@link Serializable}
	 * protocol.
	 * @Redis SET
	 * @param <T>
	 * @param key
	 * @param object
	 * @see {@link JRedis#set(String, byte[])}
	 */
	public <T extends Serializable> 
		   Future<ResponseStatus> set (String key, T object);

	/**
	 * @Redis SETNX
	 * @param key
	 * @param value
	 * @return
	 */
	public Future<Boolean> setnx (String key, byte[] value);
	public Future<Boolean> setnx (String key, String stringValue);
	public Future<Boolean> setnx (String key, Number numberValue);
	public <T extends Serializable> 
		   Future<Boolean> setnx (String key, T object);

	/**
	 * @Redis GET
	 * @param key
	 * @return
	 */
	public Future<byte[]> get (String key) ;

	public Future<byte[]> getset (String key, byte[] value);
	public Future<byte[]> getset (String key, String stringValue);
	public Future<byte[]> getset (String key, Number numberValue);
	public <T extends Serializable> 
		Future<byte[]> getset (String key, T object);

	
	/**
	 * @Redis MGET
	 * @param key
	 * @param moreKeys
	 * @return
	 */
	public Future<List<byte[]>> mget(String key, String...moreKeys);

	/**
	 * @Redis INCR
	 * @param key
	 * @return
	 */
	public Future<Long> incr (String key);

	/**
	 * @Redis INCRBY
	 * @param key
	 * @param delta
	 * @return
	 */
	public Future<Long> incrby (String key, int delta);

	/**
	 * @Redis DECR
	 * @param key
	 * @return
	 */
	public Future<Long> decr (String key);

	/**
	 * @Redis DECRBY
	 * @param key
	 * @param delta
	 * @return
	 */
	public Future<Long> decrby (String key, int delta);

	/**
	 * @Redis EXISTS
	 * @param key
	 * @return
	 */
	public Future<Boolean> exists(String key);

	/**
	 * @Redis DEL
	 * @param key
	 * @return
	 */
	public Future<Boolean> del (String key);

	/**
	 * @Redis TYPE
	 * @param key
	 * @return
	 */
	public Future<RedisType> type (String key);
	
	
	// ------------------------------------------------------------------------
	// "Commands operating on the key space"
	// ------------------------------------------------------------------------
	
	/**
	 * @Redis KEYS
	 * @param pattern
	 * @return
	 */
	public Future<List<String>> keys (String pattern);
	
	/**
	 * Convenience method.  Equivalent to calling <code>jredis.keys("*");</code>
	 * @Redis KEYS
	 * @return
	 * @see {@link JRedis#keys(String)}
	 */
	public Future<List<String>> keys ();

	/**
	 * @Redis RANDOMKEY
	 * @return
	 */
	public Future<String> randomkey();
	
	/**
	 * @Redis RENAME
	 * @param oldkey
	 * @param newkey
	 */
	public Future<ResponseStatus> rename (String oldkey, String newkey);
	
	/**
	 * @Redis RENAMENX
	 * @param oldkey
	 * @param brandnewkey
	 * @return
	 */
	public Future<Boolean> renamenx (String oldkey, String brandnewkey);
	
	/**
	 * @Redis DBSIZE
	 * @return
	 */
	public Future<Long> dbsize ();
	
	/**
	 * @Redis EXPIRE
	 * @param key
	 * @param ttlseconds
	 * @return
	 */
	public Future<Boolean> expire (String key, int ttlseconds); 
	
	/**
	 * @Redis TTL
	 * @param key
	 * @return
	 */
	public Future<Long> ttl (String key);
	
	// ------------------------------------------------------------------------
	// Commands operating on lists
	// ------------------------------------------------------------------------

	/**
	 * @Redis RPUSH
	 * @param listkey
	 * @param value
	 */
	public Future<ResponseStatus> rpush (String listkey, byte[] value);
	public Future<ResponseStatus> rpush (String listkey, String stringValue);
	public Future<ResponseStatus> rpush (String listkey, Number numberValue);
	public <T extends Serializable> 
		Future<ResponseStatus> rpush (String listkey, T object);
	
	/**
	 * @Redis LPUSH
	 * @param listkey
	 * @param value
	 */
	public Future<ResponseStatus> lpush (String listkey, byte[] value);
	public Future<ResponseStatus> lpush (String listkey, String stringValue);
	public Future<ResponseStatus> lpush (String listkey, Number numberValue);
	public <T extends Serializable> 
		Future<ResponseStatus> lpush (String listkey, T object);
	
	/**
	 * @Redis LSET
	 * @param key
	 * @param index
	 * @param value
	 */
	public Future<ResponseStatus> lset (String key, long index, byte[] value);
	public Future<ResponseStatus> lset (String key, long index, String stringValue);
	public Future<ResponseStatus> lset (String key, long index, Number numberValue);
	public <T extends Serializable> 
		Future<ResponseStatus> lset (String key, long index, T object);
	

	/**
	 * @Redis LREM
	 * @param listKey
	 * @param value
	 * @param count
	 * @return
	 */
	public Future<Long> lrem (String listKey, byte[] value,       int count);
	public Future<Long> lrem (String listKey, String stringValue, int count);
	public Future<Long> lrem (String listKey, Number numberValue, int count);
	public <T extends Serializable> 
		Future<Long> lrem (String listKey, T object, int count);
	
	/**
	 * Given a 'list' key, returns the number of items in the list.
	 * @Redis LLEN
	 * @param listkey
	 * @return
	 */
	public Future<Long> llen (String listkey);
	
	/**
	 * @Redis LRANGE
	 * @param listkey
	 * @param from
	 * @param to
	 * @return
	 */
	public Future<List<byte[]>> lrange (String listkey, long from, long to); 

	/**
	 * @Redis LTRIM
	 * @param listkey
	 * @param keepFrom
	 * @param keepTo
	 */
	public Future<ResponseStatus> ltrim (String listkey, long keepFrom, long keepTo);
	
	/**
	 * @Redis LINDEX
	 * @param listkey
	 * @param index
	 * @return
	 */
	public Future<byte[]> lindex (String listkey, long index);
	
	/**
	 * @Redis LPOP
	 * @param listKey
	 * @return
	 */
	public Future<byte[]> lpop (String listKey);
	
	/**
	 * @Redis RPOP
	 * @param listKey
	 * @return
	 */
	public Future<byte[]> rpop (String listKey);

	// ------------------------------------------------------------------------
	// Commands operating on sets
	// ------------------------------------------------------------------------
	
	/**
	 * @Redis SADD
	 * @param setkey
	 * @param member
	 * @return
	 */
	public Future<Boolean> sadd (String setkey, byte[] member);
	public Future<Boolean> sadd (String setkey, String stringValue);
	public Future<Boolean> sadd (String setkey, Number numberValue);
	public <T extends Serializable> 
		Future<Boolean> sadd (String setkey, T object);

	/**
	 * @Redis SREM
	 * @param setKey
	 * @param member
	 * @return
	 */
	public Future<Boolean> srem (String setKey, byte[] member);
	public Future<Boolean> srem (String setKey, String stringValue);
	public Future<Boolean> srem (String setKey, Number numberValue);
	public <T extends Serializable> 
		Future<Boolean> srem (String setKey, T object);

	/**
	 * @Redis SISMEMBER
	 * @param setKey
	 * @param member
	 * @return
	 */
	public Future<Boolean> sismember (String setKey, byte[] member);
	public Future<Boolean> sismember (String setKey, String stringValue);
	public Future<Boolean> sismember (String setKey, Number numberValue);
	public <T extends Serializable> 
		Future<Boolean> sismember (String setKey, T object);
	
	/**
	 * @Redis SMOVE
	 * @param srcKey
	 * @param destKey
	 * @param member
	 * @return
	 */
	public Future<Boolean> smove (String srcKey, String destKey, byte[] member);
	public Future<Boolean> smove (String srcKey, String destKey, String stringValue);
	public Future<Boolean> smove (String srcKey, String destKey, Number numberValue);
	public <T extends Serializable> 
		Future<Boolean> smove (String srcKey, String destKey, T object);
	
	/**
	 * @Redis SCARD
	 * @param setKey
	 * @return
	 */
	public Future<Long> scard (String setKey);	
	
	/**
	 * @Redis SINTER
	 * @param set1
	 * @param sets
	 * @return
	 */
	public Future<List<byte[]>> sinter (String set1, String...sets);
	/**
	 * @Redis SINTERSTORE
	 * @param destSetKey
	 * @param sets
	 */
	public Future<ResponseStatus> sinterstore (String destSetKey, String...sets);

	/**
	 * @Redis SUNION
	 * @param set1
	 * @param sets
	 * @return
	 */
	public Future<List<byte[]>> sunion (String set1, String...sets);
	
	/**
	 * @Redis SUNIONSTORE
	 * @param destSetKey
	 * @param sets
	 */
	public Future<ResponseStatus> sunionstore (String destSetKey, String...sets);

	/**
	 * @Redis SDIFF
	 * @param set1
	 * @param sets
	 * @return
	 */
	public Future<List<byte[]>> sdiff (String set1, String...sets);
	
	/**
	 * @Redis SDIFFSTORE
	 * @param destSetKey
	 * @param sets
	 */
	public Future<ResponseStatus> sdiffstore (String destSetKey, String...sets);

	/**
	 * @Redis SMEMBERS
	 * @param setkey
	 * @return
	 */
	public Future<List<byte[]>> smembers (String setkey);
	
	// ------------------------------------------------------------------------
	// Multiple databases handling commands
	// ------------------------------------------------------------------------
	
//	@Deprecated
//	public Future<ResponseStatus> select (int index);

	/**
	 * Flushes the db you selected when connecting to Redis server.  Typically,
	 * implementations will select db 0 on connecting if non was specified.  Remember
	 * that there is no roll-back.
	 * @Redis FLUSHDB
	 * @return
	 */
	public Future<ResponseStatus> flushdb ();

	/**
	 * Flushes all dbs in the connect Redis server, regardless of which db was selected
	 * on connect time.  Remember that there is no rollback.
	 * @Redis FLUSHALL
	 * @return
	 */
	public Future<ResponseStatus> flushall ();

	/**
	 * Moves the given key from the currently selected db to the one indicated
	 * by <code>dbIndex</code>.
	 * @Redis MOVE
	 * @param key
	 * @param dbIndex
	 * @return
	 */
	public Future<Boolean> move (String key, int dbIndex);
	
	// ------------------------------------------------------------------------
	// Sorting
	// ------------------------------------------------------------------------
	
	/**
	 * <p>For Usage details regarding sort semantics, see {@link JRedis#sort}.  The
	 * only difference in usage is that you must use the {@link Sort#execAsynch()} method
	 * which returns a {@link Future} instances.
	 * <p>Usage:
	 * <p><code><pre>
	 * Future<List<byte[]>>  futureResults = redis.sort("my-list-or-set-key").BY("weight*").LIMIT(1, 11).GET("object*").DESC().ALPHA().execAsynch();
	 * List<byte[]> results = futureResult.get();  // wait for the asynchronous response to be processed
	 * for(byte[] item : results) {
	 *     // do something with item ..
	 *  }
	 * </pre></code>
	 * 
	 * @Redis SORT
	 * @see Redis
	 * @see Future
	 * 
	 */
	public Sort sort(String key);
	
	// ------------------------------------------------------------------------
	// Persistence control commands
	// ------------------------------------------------------------------------

	/**
	 * @Redis SAVE
	 */
	public Future<ResponseStatus> save();

	/**
	 * @Redis BGSAVE
	 */
	public Future<ResponseStatus> bgsave ();

	/**
	 * @Redis LASTSAVE
	 * @return
	 */
	public Future<Long> lastsave ();


// ------------------------------------------------------------------------
// Remote server control commands
// ------------------------------------------------------------------------

	/**
	 * @Redis INFO
	 * @return
	 */
	public Future<Map<String, String>>	info () ;
}