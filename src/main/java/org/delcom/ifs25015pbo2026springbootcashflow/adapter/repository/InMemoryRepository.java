package org.delcom.ifs25015pbo2026springbootcashflow.adapter.repository;

import org.delcom.ifs25015pbo2026springbootcashflow.domain.repository.IRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Basis generik penyimpanan di memori untuk entitas apa pun.
 * Urutan penyimpanan dipertahankan; {@link #findAll()} mengembalikan salinan defensif.
 *
 * @param <T>  tipe entitas
 * @param <ID> tipe identifier
 */
public abstract class InMemoryRepository<T, ID> implements IRepository<T, ID> {

    private final Map<ID, T> store = new LinkedHashMap<>();
    private final Function<T, ID> idExtractor;

    protected InMemoryRepository(Function<T, ID> idExtractor) {
        this.idExtractor = idExtractor;
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public T save(T entity) {
        store.put(idExtractor.apply(entity), entity);
        return entity;
    }

    @Override
    public boolean deleteById(ID id) {
        return store.remove(id) != null;
    }

    @Override
    public void update(T entity) {
        store.replace(idExtractor.apply(entity), entity);
    }

    @Override
    public List<T> findBy(Predicate<T> predicate) {
        return store.values().stream().filter(predicate).toList();
    }
}
