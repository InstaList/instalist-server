package org.noorganization.instalist.server.controller.impl;

import org.noorganization.instalist.server.controller.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * This Factory points to always up-to-date controllers (for decoupling).
 * Created by damihe on 05.02.16.
 */
public class ControllerFactory {

    public static IAuthController getAuthController() {
        return new AuthController();
    }

    public static ICategoryController getCategoryController(EntityManager _manager) {
        return new CategoryController(_manager);
    }

    public static IEntryController getEntryController(EntityManager _manager) {
        return new EntryController(_manager);
    }

    public static IIngredientController getIngredientController(EntityManager _manager) {
        return new IngredientController(_manager);
    }

    public static IGroupController getGroupController(EntityManager _manager) {
        return new GroupController(_manager);
    }

    public static IListController getListController(EntityManager _manager) {
        return new ListController(_manager);
    }

    public static IProductController getProductController(EntityManager _manager) {
        return new ProductController(_manager);
    }

    public static IRecipeController getRecipeController(EntityManager _manager) {
        return new RecipeController(_manager);
    }

    public static ITagController getTagController(EntityManager _manager) {
        return new TagController(_manager);
    }

    public static ITaggedProductController getTaggedProductController(EntityManager _manager) {
        return new TaggedProductController(_manager);
    }

    public static IUnitController getUnitController(EntityManager _manager) {
        return new UnitController(_manager);
    }

    private ControllerFactory(){
    }
}
