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
import org.noorganization.instalist.server.model.TaggedProduct;
import org.noorganization.instalist.server.support.exceptions.ConflictException;
import org.noorganization.instalist.server.support.exceptions.GoneException;

import javax.ws.rs.BadRequestException;
import java.time.Instant;
import java.util.UUID;

/**
 * A controller for changing tagged products.
 */
public interface ITaggedProductController extends IFinder<TaggedProduct> {
    /**
     * Creates a tagged product.
     * @param _groupId The id of the group that should contain the ingredient.
     * @param _tpUUID The uuid of the tagged product identifying it in the group.
     * @param _tagUUID The uuid of the tag.
     * @param _productUUID The uuid of the product.
     * @param _lastChanged A change date.
     * @throws ConflictException If already a tagged product with same uuid exists or it was already
     * deleted after {@code _lastChanged}.
     * @throws BadRequestException If either {@code _tagUUID} or {@code _productUUID} could
     * not be resolved.
     */
    void add(int _groupId, UUID _tpUUID, UUID _tagUUID, UUID _productUUID, Instant _lastChanged)
            throws ConflictException, BadRequestException;

    /**
     * Updates a tagged product. Even if this seems currently senseless, the usage of this method
     * is reserved for future. It is kept for consistency with other interfaces.
     * @param _groupId The id of the group that contains the tagged product.
     * @param _tpUUID The uuid of the tagged product identifying it in the group.
     * @param _tagUUID The uuid of the tag. May be null for keeping it.
     * @param _productUUID The uuid of the product. May be null for keeping it.
     * @param _lastChanged A change date.
     * @throws ConflictException If a change was made before.
     * @throws GoneException If tagged product was deleted before.
     * @throws NotFoundException If tagged product was not found.
     * @throws BadRequestException If either {@code _tagUUID} or {@code _productUUID} could
     * not be resolved.
     */
    void update(int _groupId, UUID _tpUUID, UUID _tagUUID, UUID _productUUID, Instant _lastChanged)
            throws ConflictException, GoneException, NotFoundException, BadRequestException;

    /**
     * Deletes a connection between tag and product.
     * @param _groupId The id of the group containing the tag.
     * @param _tpUUID The uuid of the tagged product identifying it in the group.
     * @throws GoneException If tagged product ingredient was deleted before.
     * @throws NotFoundException If tagged product was not found.
     */
    void delete(int _groupId, UUID _tpUUID) throws GoneException, NotFoundException;
}
