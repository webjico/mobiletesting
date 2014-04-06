package com.example.cs6300todolist.entity;

import com.example.cs6300todolist.entity.PMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JDOCursorHelper;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

@Api(name = "todoitemendpoint", namespace = @ApiNamespace(ownerDomain = "example.com", ownerName = "example.com", packagePath = "cs6300todolist.entity"))
public class ToDoItemEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listToDoItem")
	public CollectionResponse<ToDoItem> listToDoItem(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<ToDoItem> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(ToDoItem.class);
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<ToDoItem>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (ToDoItem obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<ToDoItem> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getToDoItem")
	public ToDoItem getToDoItem(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		ToDoItem todoitem = null;
		try {
			todoitem = mgr.getObjectById(ToDoItem.class, id);
		} finally {
			mgr.close();
		}
		return todoitem;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param todoitem the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertToDoItem")
	public ToDoItem insertToDoItem(ToDoItem todoitem) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (containsToDoItem(todoitem)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.makePersistent(todoitem);
		} finally {
			mgr.close();
		}
		return todoitem;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param todoitem the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateToDoItem")
	public ToDoItem updateToDoItem(ToDoItem todoitem) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsToDoItem(todoitem)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(todoitem);
		} finally {
			mgr.close();
		}
		return todoitem;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeToDoItem")
	public void removeToDoItem(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			ToDoItem todoitem = mgr.getObjectById(ToDoItem.class, id);
			mgr.deletePersistent(todoitem);
		} finally {
			mgr.close();
		}
	}

	private boolean containsToDoItem(ToDoItem todoitem) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(ToDoItem.class, todoitem.getId());
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			contains = false;
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
