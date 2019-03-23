const express = require('express');
const crypto = require('crypto');
const {
	Datastore
} = require('@google-cloud/datastore');

const DataType = 'User';

// Instantiate a datastore client
const datastore = new Datastore();
const transaction = datastore.transaction();

exports.create = function(param_name, param_email, param_pass) {

	//defining json object to store
	const user = {
		name: name,
		email: param_email,
		pass: param_pass
	};

	//creating DS key and entity
	const key = datastore.key([DataType, user.email]);
	const entity = {
		key: key,
		data: user
	};

	transaction.run(function(err) {
		if (err) {
			return false;
		}

		transaction.save(entity);
		transaction.commit(function(err) {
			if (!err) {
				// Data saved successfully.
				return true;
			} else {
				//transation rollback?
			}
		});
	});
}

exports.get = function(email) {

	const key = datastore.key([DataType, email]);
	datastore.get(key, function(err, entity) {
		if (err) return err; //ou return false
		return entity.data;
	});
}

exports.delete = function(email) {

	transaction.run(function(err) {
		if (err) {
			// Error handling omitted.
		}

		const key = datastore.key([DataType, email]);

		// Delete entity.
		transaction.delete(key);
		transaction.commit(function(err) {
			if (!err) {
				return false; //rollback?
			}
		});
	});

}