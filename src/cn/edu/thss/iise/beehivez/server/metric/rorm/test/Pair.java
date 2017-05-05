package cn.edu.thss.iise.beehivez.server.metric.rorm.test;

@SuppressWarnings("rawtypes")
public class Pair<K, V extends Comparable> implements Comparable<Pair<K, V>>{
	public K key;
	public V value;
	
	public Pair(K _key, V _value) {
		this.key = _key;
		this.value = _value;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(Pair<K, V> o) {
		// TODO Auto-generated method stub
		return this.value.compareTo(o.value);
	}
}
