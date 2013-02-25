package de.tudarmstadt.ukp.dkpro.core.api.frequency.tfidf.util;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * An object that counts objects. Inspired by NLTKs FreqDist-class.
 *
 * @author Mateusz Parzonka
 *
 * @param <T>
 *            The type of element which is counted.
 */
public class FreqDist<T>
	implements Serializable
{

	private static final long serialVersionUID = 9155968779719980277L;

	private Map<T, Integer> counts;
	private int totalCount = 0;

	/**
	 * Instantiate a FreqDist and count the given collection.
	 *
	 * @param collection
	 */
	public FreqDist(Collection<T> collection)
	{
		super();
		counts = new HashMap<T, Integer>();
		count(collection);
	}

	/**
	 * Creates an empty FreqDist.
	 */
	public FreqDist()
	{
		super();
		counts = new HashMap<T, Integer>();
	}

	/**
	 * Returns a mapping of elements to probabilities of their occurrence.
	 *
	 * @return probabilities of occurrence for every element
	 */
	public Map<T, Double> getProbabilities()
	{
		Map<T, Double> probabilities = new HashMap<T, Double>();
		for (Entry<T, Integer> e : counts.entrySet()) {
			probabilities.put(e.getKey(), (double) e.getValue() / totalCount);
		}
		return probabilities;
	}

	/**
	 * Returns the count of the given element
	 *
	 * @param element
	 * @return the count
	 */
	public int getCount(T element)
	{
		if (counts.containsKey(element))
			return counts.get(element);
		else
			return 0;
	}

	/**
	 * Increment the count for the given element.
	 *
	 * @param element
	 *            the element to be counted
	 */
	public void count(T element)
	{
		totalCount++;
		if (counts.containsKey(element)) {
			counts.put(element, counts.get(element) + 1);
		}
		else {
			counts.put(element, 1);
		}
	}

	/**
	 * Increment the counts for all elements contained in the collection. When
	 * elements are contained multiple times, they are counted multiple times as
	 * well.
	 *
	 * @param collection
	 *            a collection of elements
	 */
	public void count(Collection<T> collection)
	{
		for (T element : collection) {
			count(element);
		}
	}

	/**
	 * Returns the accumulated count of all elements.
	 *
	 * @return the total count
	 */
	public int getTotalCount()
	{
		return totalCount;
	}

}