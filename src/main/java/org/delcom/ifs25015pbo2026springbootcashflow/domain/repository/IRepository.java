package org.delcom.ifs25015pbo2026springbootcashflow.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Port penyimpanan generik.
 *
 * @param <T>  tipe entitas
 * @param <ID> tipe identifier entitas
 */
public interface IRepository<T, ID> {

    /** Semua entitas (salinan defensif). */
    List<T> findAll();

    Optional<T> findById(ID id);

    /** Menyimpan entitas baru. */
    T save(T entity);

    /** @return true bila ada entitas yang terhapus */
    boolean deleteById(ID id);

    /** Menyimpan perubahan entitas yang sudah ada. */
    void update(T entity);

    /** Entitas yang memenuhi predikat. */
    List<T> findBy(Predicate<T> predicate);
}
