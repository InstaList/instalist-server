
package org.noorganization.instalist.server.api;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import org.noorganization.instalist.comm.support.DateHelper;
import org.noorganization.instalist.server.CommonEntity;
import org.noorganization.instalist.server.TokenSecured;
import org.noorganization.instalist.comm.message.CategoryInfo;
import org.noorganization.instalist.server.controller.ICategoryController;
import org.noorganization.instalist.server.controller.impl.ControllerFactory;
import org.noorganization.instalist.server.message.Error;
import org.noorganization.instalist.server.model.Category;
import org.noorganization.instalist.server.model.DeletedObject;
import org.noorganization.instalist.server.model.DeviceGroup;
import org.noorganization.instalist.server.support.exceptions.ConflictException;
import org.noorganization.instalist.server.support.DatabaseHelper;
import org.noorganization.instalist.server.support.exceptions.GoneException;
import org.noorganization.instalist.server.support.ResponseFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Collection of available categories.
 * 
 */
@Path("/groups/{groupid}/categories")
public class CategoriesResource {


    /**
     * Get a list of categories.
     *
     * @param _groupId The id of the group.
     * @param _changedSince Optional. Requests only the elements that changed since the given date.
     *                      ISO 8601 time e.g. 2016-01-19T11:54:07+01:00
     */
    @GET
    @TokenSecured
    @Produces({ "application/json" })
    public Response getCategories(@PathParam("groupid") int _groupId,
                                  @QueryParam("changedsince") String _changedSince)
            throws Exception {
        Date changedSince = null;
        if (_changedSince != null) {
            changedSince = DateHelper.parseDate(_changedSince);
            if (changedSince == null)
                return ResponseFactory.generateBadRequest(CommonEntity.sInvalidData);
        }

        List<Category> categories;
        List<DeletedObject> deletedCategories;
        EntityManager manager = DatabaseHelper.getInstance().getManager();
        DeviceGroup group = manager.find(DeviceGroup.class, _groupId);

        if (changedSince != null) {
            TypedQuery<Category> categoriesQuery =
                    manager.createQuery("select c from Category c " +
                                    "where c.group = :groupid and c.updated > :updated",
                            Category.class);
            categoriesQuery.setParameter("groupid", group);
            categoriesQuery.setParameter("updated", changedSince);
            categories = categoriesQuery.getResultList();

            TypedQuery<DeletedObject> deletedCategoriesQuery =
                    manager.createQuery("select do " +
                                    "from DeletedObject do where do.group = :groupid and " +
                                    "do.type = :type and do.time > :time",
                            DeletedObject.class);
            deletedCategoriesQuery.setParameter("groupid", group);
            deletedCategoriesQuery.setParameter("time", changedSince);
            deletedCategoriesQuery.setParameter("type", DeletedObject.Type.CATEGORY);
            deletedCategories = deletedCategoriesQuery.getResultList();
        } else {
            TypedQuery<Category> categoriesQuery =
                    manager.createQuery("select c from Category c " +
                            "where c.group = :groupid", Category.class);
            categoriesQuery.setParameter("groupid", group);
            categories = categoriesQuery.getResultList();

            TypedQuery<DeletedObject> deletedCategoriesQuery =
                    manager.createQuery("select do " +
                                    "from DeletedObject do where do.group = :groupid and " +
                                    "do.type = :type",
                            DeletedObject.class);
            deletedCategoriesQuery.setParameter("groupid", group);
            deletedCategoriesQuery.setParameter("type", DeletedObject.Type.CATEGORY);
            deletedCategories = deletedCategoriesQuery.getResultList();
        }
        manager.close();

        List<CategoryInfo> rtnPayload = new ArrayList<CategoryInfo>(categories.size() +
                deletedCategories.size());
        for (Category currentCat: categories) {
            CategoryInfo info = new CategoryInfo();
            info.setUUID(currentCat.getUUID());
            info.setName(currentCat.getName());
            info.setLastChanged(currentCat.getUpdated());
            info.setDeleted(false);
            rtnPayload.add(info);
        }
        for (DeletedObject currentCat: deletedCategories) {
            CategoryInfo info = new CategoryInfo();
            info.setUUID(currentCat.getUUID());
            info.setLastChanged(currentCat.getTime());
            info.setDeleted(true);
            rtnPayload.add(info);
        }

        return ResponseFactory.generateOK(rtnPayload);
    }

    /**
     * Updates the category.
     * @param _uuid The uuid of the category to update.
     * @param _entity A category with updated information.
     */
    @PUT
    @TokenSecured
    @Path("{categoryuuid}")
    @Consumes("application/json")
    @Produces({ "application/json" })
    public Response putCategory(@PathParam("groupid") int _groupId,
                                @PathParam("categoryuuid") String _uuid,
                                CategoryInfo _entity) throws Exception {
        try{

        if (_entity.getName() == null)
            return ResponseFactory.generateBadRequest(CommonEntity.sNoData);
        if ((_entity.getUUID() != null && !_entity.getUUID().equals(_uuid)) ||
                (_entity.getDeleted() != null && _entity.getDeleted()))
            return ResponseFactory.generateBadRequest(CommonEntity.sInvalidData);

        Date changedDate;
        if (_entity.getLastChanged() != null) {
            changedDate = DateHelper.parseDate(_entity.getLastChanged());
            if (changedDate == null || changedDate.after(new Date())) {
                return ResponseFactory.generateBadRequest(CommonEntity.INVALID_DATE);
            }
        } else
            changedDate = new Date(System.currentTimeMillis());
        UUID categoryUUID;
        try {
            categoryUUID = UUID.fromString(_uuid);
        } catch (IllegalArgumentException e) {
            return ResponseFactory.generateBadRequest(CommonEntity.INVALID_UUID);
        }

        EntityManager manager = DatabaseHelper.getInstance().getManager();
        ICategoryController categoryController =
                ControllerFactory.getCategoryController(manager);
        try {
            categoryController.update(_groupId, categoryUUID, _entity.getName(), changedDate);
        } catch (NotFoundException e) {
            return ResponseFactory.generateNotFound(new Error().withMessage("Category was not " +
                            "found."));
        } catch (GoneException e) {
            return ResponseFactory.generateGone(new Error().withMessage("Category was already " +
                            "deleted."));
        } catch (ConflictException e) {
            return ResponseFactory.generateConflict(new Error().withMessage("Sent sategory is in " +
                    "conflict with saved one."));
        } finally {
            manager.close();
        }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return ResponseFactory.generateOK(null);
    }

    /**
     * Creates the category.
     * @param _token Authorization-token for the current user.
     * @param _entity Information for the new category.
     *      e.g. examples/category.example
     */
    @POST
    @TokenSecured
    @Consumes("application/json")
    @Produces({ "application/json" })
    public Response postCategory(@PathParam("groupid") int _groupId, CategoryInfo _entity) throws
            Exception {
        return null;
    }

    /**
     * Deletes the category.
     * 
     * 
     * @param _categoryId The uuid of the category to delete.
     * @param _token
     *     
     */
    @DELETE
    @TokenSecured
    @Path("{categoryuuid}")
    @Produces({ "application/json" })
    public Response deleteCategory(@PathParam("groupid") int _groupId,
                                   @PathParam("categoryuuid") String _uuid) throws Exception {
        return null;
    }

}
