package io.limberapp.backend.module.orgs.store.org

import com.google.inject.Inject
import com.piperframework.store.SqlStore
import io.limberapp.backend.module.orgs.entity.org.FeatureTable
import io.limberapp.backend.module.orgs.exception.conflict.FeatureIsNotUnique
import io.limberapp.backend.module.orgs.exception.notFound.FeatureNotFound
import io.limberapp.backend.module.orgs.model.org.FeatureModel
import io.limberapp.backend.module.orgs.service.org.FeatureService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.util.UUID

internal class SqlFeatureStore @Inject constructor(
    database: Database
) : FeatureService, SqlStore(database) {

    override fun create(orgId: UUID, models: List<FeatureModel>) = transaction<Unit> {
        FeatureTable.batchInsert(models) { model ->
            this[FeatureTable.createdDate] = model.created
            this[FeatureTable.guid] = model.id
            this[FeatureTable.orgGuid] = orgId
            this[FeatureTable.name] = model.name
            this[FeatureTable.path] = model.path
            this[FeatureTable.type] = model.type.name
        }
    }

    override fun create(orgId: UUID, model: FeatureModel) = transaction<Unit> {

        FeatureTable.select {
            (FeatureTable.orgGuid eq orgId) and (FeatureTable.path eq model.path)
        }.singleOrNull()?.let { throw FeatureIsNotUnique() }

        FeatureTable.insert { s ->
            s[createdDate] = model.created
            s[guid] = model.id
            s[orgGuid] = orgId
            s[name] = model.name
            s[path] = model.path
            s[type] = model.type.name
        }
    }

    override fun get(orgId: UUID, featureId: UUID) = transaction {
        return@transaction FeatureTable.select { (FeatureTable.orgGuid eq orgId) and (FeatureTable.guid eq featureId) }
            .singleOrNull()?.toFeatureModel()
    }

    override fun getByOrgId(orgId: UUID) = transaction {
        return@transaction FeatureTable.select { (FeatureTable.orgGuid eq orgId) }
            .map { it.toFeatureModel() }
    }

    override fun update(orgId: UUID, featureId: UUID, update: FeatureModel.Update) = transaction {

        update.path?.let {
            FeatureTable.select {
                (FeatureTable.orgGuid eq orgId) and (FeatureTable.path eq it)
            }.singleOrNull()?.let { throw FeatureIsNotUnique() }
        }

        FeatureTable.update({ (FeatureTable.orgGuid eq orgId) and (FeatureTable.guid eq featureId) }) { s ->
            update.name?.let { s[name] = it }
            update.path?.let { s[path] = it }
        }
            .ifEq(0) { throw FeatureNotFound() }
            .ifGt(1, ::badSql)
        return@transaction checkNotNull(get(orgId, featureId))
    }

    override fun delete(orgId: UUID, featureId: UUID) = transaction<Unit> {
        FeatureTable.deleteAtMostOneWhere { (FeatureTable.orgGuid eq orgId) and (FeatureTable.guid eq featureId) }
            .ifEq(0) { throw FeatureNotFound() }
    }

    private fun ResultRow.toFeatureModel() = FeatureModel(
        id = this[FeatureTable.guid],
        created = this[FeatureTable.createdDate],
        name = this[FeatureTable.name],
        path = this[FeatureTable.path],
        type = FeatureModel.Type.valueOf(this[FeatureTable.type])
    )
}
