package br.com.bit.ideias.reflection.rql.query;

import static java.lang.String.format;

import java.lang.reflect.Member;
import java.util.List;

import br.com.bit.ideias.reflection.cache.Cache;
import br.com.bit.ideias.reflection.cache.CacheProvider;
import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.exceptions.NoResultException;
import br.com.bit.ideias.reflection.exceptions.TooManyResultException;

/**
 * @author Leonardo Campos
 * @date 01/12/2009
 */
public class QueryImpl implements Query {
    private final Introspector introspector;
    private final String       query;
    private Cache              cache;

    public QueryImpl(Introspector introspector, String query) {
        this.introspector = introspector;
        this.query = query;
        this.cache = CacheProvider.getCache();
    }

    public QueryImpl(String query) {
        this(null, query);
    }

    @SuppressWarnings("unchecked")
    public <T extends Member> List<T> list() {
        String finalQuery = finalQuery();
        String loweredQuery = lower(finalQuery);
        
        List<T> members = (List<T>) cache.get(loweredQuery);
        if (members != null)
            return members;

        members = Rql.getInstance().parse(finalQuery).list();

        cache.add(loweredQuery, members);

        return members;
    }

    private String lower(String text) {
        if(text == null) return null;
        return text.toLowerCase();
    }

    @SuppressWarnings("unchecked")
    public <T extends Member> T uniqueResult() {
        final List result = list();
        if (result.isEmpty())
            throw new NoResultException();

        if (result.size() > 1)
            throw new TooManyResultException();

        return (T) result.get(0);
    }

    private String finalQuery() {
        if (introspector == null)
            return query;
        String initQuery = format("FROM %s ", introspector.getTargetClass().getName());
        if (query == null)
            return initQuery;

        if (query.trim().toLowerCase().startsWith("where")) return initQuery + query;
        if (query.trim().toLowerCase().startsWith("from")) return query;
        
        return initQuery + "WHERE " + query;
    }
}
