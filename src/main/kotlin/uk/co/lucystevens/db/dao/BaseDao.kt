package uk.co.lucystevens.db.dao

import org.ktorm.entity.Entity
import org.ktorm.entity.EntitySequence
import org.ktorm.entity.add
import org.ktorm.entity.toList
import org.ktorm.entity.update
import org.ktorm.schema.Table

abstract class BaseDao<E: Entity<E>, T: Table<E>> {

    abstract val entitySequence: EntitySequence<E, T>
    val tableName by lazy { entitySequence.sourceTable.tableName }

    fun list(): List<E> =
        entitySequence.toList()

    fun update(entity: E) =
        entitySequence.update(entity)

    fun add(entity: E) =  entitySequence.add(entity)

}