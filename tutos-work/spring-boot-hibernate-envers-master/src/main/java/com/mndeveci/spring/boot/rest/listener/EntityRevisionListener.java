package com.mf.spring.boot.rest.listener;


import org.hibernate.envers.RevisionListener;

/**
 * Created by mf on 21.11.2016.
 */
public class EntityRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object o) {
        System.out.println("New revision is created: " + o);
    }
}
