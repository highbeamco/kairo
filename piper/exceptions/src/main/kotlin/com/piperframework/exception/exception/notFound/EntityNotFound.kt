package com.piperframework.exception.exception.notFound

abstract class EntityNotFound(entityName: String) : NotFoundException("$entityName not found.")