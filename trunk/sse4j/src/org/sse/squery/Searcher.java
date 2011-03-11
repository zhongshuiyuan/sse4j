package org.sse.squery;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.sse.io.IdxReader;
import org.sse.io.Sidxer;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.SpatialIndex;

/**
 * 
 * @author dux(duxionggis@126.com)
 * 
 */
public class Searcher {
	private static Searcher instance;
	private static Object lock = new Object();
	private Map<String, SQuery> queries;

	protected Searcher() {
		queries = new HashMap<String, SQuery>();
	}

	public static Searcher getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new Searcher();
				}
			}
		}
		return instance;
	}

	public Set<String> getKeys() {
		return queries.keySet();
	}

	/**
	 * set SQuery metadata
	 * 
	 * @param key
	 *            Map's key
	 * @param reader
	 *            index reader object
	 * @param tree
	 *            spatial index object
	 * @param extent
	 *            spatial index's full extent
	 */
	public void put(String key, IdxReader reader, SpatialIndex tree,
			Envelope extent) {
		queries.put(key, new SQuery(reader, tree, extent));
	}

	/**
	 * check Searcher contains named key's squery
	 * 
	 * @param key
	 *            Map's key
	 * @param idxpath
	 *            support multi-paths separated by ','
	 * @param wgs
	 * @return
	 */
	public boolean check(String key, String idxpath, boolean wgs) {
		try {
			if (!queries.containsKey(key)) {
				Sidxer idxer = new Sidxer();
				File file = new File(idxpath.split(",")[0] + ".sidx");
				if (!file.exists())
					idxer.save(idxpath, PtyName.OID, wgs);
				else
					idxer.read(idxpath.split(",")[0] + ".sidx");
				IdxReader reader = new IdxReader(idxpath);
				queries.put(key, new SQuery(reader, idxer.getTree(), idxer
						.getExtent()));
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public List<Document> search(String key, Filter filter) {
		return queries.get(key).query(filter);
	}

	public List<Document> search(String key, List<Term> terms) {
		return queries.get(key).query(terms);
	}

	public List<Document> search(String key, Query query, int count) {
		return queries.get(key).query(query, count);
	}

	/**
	 * equals SpatialIndex.query(Envelope)
	 * 
	 * @param key
	 * @param env
	 * @return SpatialIndex's value list
	 */
	public List boxQuery(String key, Envelope env) {
		return queries.get(key).box(env);
	}

	public Envelope fullExtent(String key) {
		return queries.get(key).getExtent();
	}

}
