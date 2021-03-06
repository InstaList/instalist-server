/*
 * Copyright 2016 Tino Siegmund, Michael Wodniok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.noorganization.instalist.server.controller;

import javassist.NotFoundException;
import org.noorganization.instalist.server.controller.generic.IFinder;
import org.noorganization.instalist.server.model.DeletedObject;
import org.noorganization.instalist.server.model.DeviceGroup;
import org.noorganization.instalist.server.model.Ingredient;
import org.noorganization.instalist.server.model.Recipe;
import org.noorganization.instalist.server.support.exceptions.ConflictException;
import org.noorganization.instalist.server.support.exceptions.GoneException;

import javax.ws.rs.BadRequestException;
import java.time.Instant;
import java.util.UUID;

/**
 * A controller for changing ingredients of recipes.
 */
public interface IIngredientController extends IFinder<Ingredient> {
    /**
     * Creates an ingredient.
     * @param _groupId The id of the group that should contain the ingredient.
     * @param _ingredientUUID The uuid of the ingredient identifying it in the group.
     * @param _recipeUUID The uuid of the recipe with contains the ingredient.
     * @param _productUUID The uuid of the product with is contained by the recipe.
     * @param _amount The amount of {@code _productUUID}.
     * @param _lastChanged A change date.
     * @throws ConflictException If already a ingredient with same uuid exists or it was already
     * deleted after {@code _lastChanged}.
     * @throws BadRequestException If either {@code _recipeUUID} or {@code _productUUID} could
     * not be resolved.
     */
    void add(int _groupId, UUID _ingredientUUID, UUID _recipeUUID, UUID _productUUID,
             float _amount, Instant _lastChanged) throws ConflictException, BadRequestException;

    /**
     * Updates an ingredient.
     * @param _groupId The id of the group that contains the ingredient.
     * @param _ingredientUUID The uuid of the ingredient identifying it in the group.
     * @param _recipeUUID The uuid of the recipe with contains the ingredient.
     * @param _productUUID The uuid of the product with is contained by the recipe.
     * @param _amount The amount of {@code _productUUID}.
     * @param _lastChanged A change date.
     * @throws ConflictException If a change was made before.
     * @throws GoneException If ingredient was deleted before.
     * @throws NotFoundException If object was not found.
     * @throws BadRequestException If either {@code _recipeUUID} or {@code _productUUID} could
     * not be resolved.
     */
    void update(int _groupId, UUID _ingredientUUID, UUID _recipeUUID, UUID _productUUID,
                Float _amount, Instant _lastChanged) throws ConflictException, GoneException,
            NotFoundException, BadRequestException;

    /**
     * Deletes an recipe.
     * @param _groupId The id of the group containing the ingredient.
     * @param _ingredientUUID The uuid of the ingredient identifying it in the group.
     * @throws GoneException If ingredient was deleted before.
     * @throws NotFoundException If ingredient was not found.
     */
    void delete(int _groupId, UUID _ingredientUUID) throws GoneException, NotFoundException;
}
