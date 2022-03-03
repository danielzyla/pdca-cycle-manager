package io.github.danielzyla.pdcaApp.service;

import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

public class Sublist {
    int pageSize;
    int currentPage;
    int startItem;
    List<?> sublist;

    public Sublist(Pageable pageable) {
        this.pageSize = pageable.getPageSize();
        this.currentPage = pageable.getPageNumber();
        this.startItem = currentPage * pageSize;
        this.sublist = Collections.emptyList();
    }

    public <E> List<E> getSublist(final List<E> given) {
        if(given.size() >= this.startItem) {
            int toIndex = Math.min(startItem + pageSize, given.size());
            return given.subList(startItem, toIndex);
        } else return (List<E>) sublist;
    }
}
