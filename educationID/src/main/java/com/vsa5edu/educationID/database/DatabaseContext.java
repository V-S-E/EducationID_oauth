package com.vsa5edu.educationID.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.jinq.jpa.JPAJinqStream;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DatabaseContext {
    /*
     Здесь можно поставить @Autowired, но он отрабатывает после
     JinqJPAStramProvider, поэтому приходится выцеплять бин
     в конструкторе*/
    private EntityManager manager;
    /*
     @Autowired сначала работал, но после добавления
     сущности адреса и связей перестало
    * */
    private JinqJPAStreamProvider jinqProvider;
    private ApplicationContext context;
    @Autowired
    public DatabaseContext(ApplicationContext context){
        this.context = context;
        manager = context.getBean(EntityManager.class);
        jinqProvider = new JinqJPAStreamProvider(manager.getMetamodel());
    }

    public <E> JPAJinqStream<E> streamOf(Class<E> clazz) {
        return jinqProvider.streamAll(manager, clazz);
    }
    public EntityManager getManager(){
        EntityManager em = context.getBean(EntityManager.class);
        return em;
    }

}
