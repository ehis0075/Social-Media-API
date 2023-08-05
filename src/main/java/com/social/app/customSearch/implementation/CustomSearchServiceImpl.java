package com.social.app.customSearch.implementation;


import com.social.app.customSearch.CustomSearchService;
import com.social.app.post.dto.PostSearchRequestDTO;
import com.social.app.post.model.Post;
import com.social.app.user.dto.AppUserSearchRequestDTO;
import com.social.app.user.model.ApplicationUser;
import com.social.app.util.GeneralUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

@Slf4j
@Service
public class CustomSearchServiceImpl implements CustomSearchService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Post> searchPost(PostSearchRequestDTO searchMultipleDto) {
        log.info("::: Searching Post :::");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Post> cq = cb.createQuery(Post.class);

        Root<Post> root = cq.from(Post.class);
        List<Predicate> predicates = new ArrayList<>();


        if (valid(searchMultipleDto.getContent())) {
            predicates.add(cb.like(cb.lower(root.get("content")), '%' + searchMultipleDto.getContent().toLowerCase(Locale.ROOT) + '%'));
        }

        cq.where(predicates.toArray(new Predicate[]{}));
//        cq.orderBy(cb.desc(root.get("date")));
        TypedQuery<?> query = em.createQuery(cq);


        return (Page<Post>) getPage(searchMultipleDto.getPage(), searchMultipleDto.getSize(), query);
    }

    @Override
    public Page<ApplicationUser> searchAppUsers(AppUserSearchRequestDTO searchMultipleDto, boolean isDownload) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ApplicationUser> cq = cb.createQuery(ApplicationUser.class);

        Root<ApplicationUser> root = cq.from(ApplicationUser.class);
        List<Predicate> predicates = new ArrayList<>();

        if (valid(searchMultipleDto.getEmail())) {
            predicates.add(cb.equal(root.get("email"), searchMultipleDto.getEmail()));
        }

        if (valid(searchMultipleDto.getUsername())) {
            predicates.add(cb.equal(root.get("username"), searchMultipleDto.getUsername()));
        }

        if (valid(searchMultipleDto.getEmail())) {
            predicates.add(cb.like(cb.lower(root.get("email")), '%' + searchMultipleDto.getEmail().toLowerCase(Locale.ROOT) + '%'));
        }

        if (valid(searchMultipleDto.getUsername())) {
            predicates.add(cb.like(cb.lower(root.get("username")), '%' + searchMultipleDto.getUsername().toLowerCase(Locale.ROOT) + '%'));
        }

        cq.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<?> query = em.createQuery(cq);

        return (Page<ApplicationUser>) getPage(searchMultipleDto.getPage(), searchMultipleDto.getSize(), query);
    }

    private PageImpl<?> getPage(int page, int size, TypedQuery<?> query) {
        Pageable paged;
        int totalRows;

        paged = PageRequest.of(page, size);
        totalRows = query.getResultList().size();

        query.setFirstResult(paged.getPageNumber() * paged.getPageSize());
        query.setMaxResults(paged.getPageSize());

        return new PageImpl<>(query.getResultList(), paged, totalRows);
    }

    private boolean valid(Boolean vaBoolean) {
        return Objects.nonNull(vaBoolean);
    }

    private boolean valid(String value) {
        return !GeneralUtil.stringIsNullOrEmpty(value);
    }


}
