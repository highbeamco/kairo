package io.limberapp.backend.module.orgs.store.org

import com.google.inject.Inject
import com.piperframework.store.SqlStore
import io.limberapp.backend.module.orgs.entity.org.FeatureTable
import io.limberapp.backend.module.orgs.exception.org.FeatureIsNotUnique
import io.limberapp.backend.module.orgs.exception.org.FeatureNotFound
import io.limberapp.backend.module.orgs.model.org.FeatureModel
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateStatement
import java.util.UUID

internal class SqlFeatureStore @Inject constructor(
    database: Database,
    private val mapper: SqlOrgMapper
) : FeatureStore, SqlStore(database) {

    override fun create(orgId: UUID, models: List<FeatureModel>) = transaction<Unit> {
        FeatureTable.batchInsert(models) { model -> mapper.featureEntity(this, orgId, model) }
    }

    override fun create(orgId: UUID, model: FeatureModel) = transaction<Unit> {
        FeatureTable
            .select { (FeatureTable.orgGuid eq orgId) and (FeatureTable.path eq model.path) }
            .singleOrNull()?.let { throw FeatureIsNotUnique() }
        FeatureTable.insert { mapper.featureEntity(it, orgId, model) }
    }

    override fun get(orgId: UUID, featureId: UUID) = transaction {
        val entity = FeatureTable
            .select { (FeatureTable.orgGuid eq orgId) and (FeatureTable.guid eq featureId) }
            .singleOrNull() ?: return@transaction null
        return@transaction mapper.featureModel(entity)
    }

    override fun getByOrgId(orgId: UUID) = transaction {
        return@transaction FeatureTable
            .select { (FeatureTable.orgGuid eq orgId) }
            .map { mapper.featureModel(it) }
    }

    override fun update(orgId: UUID, featureId: UUID, update: FeatureModel.Update) = transaction {

        update.path?.let {
            FeatureTable
                .select { (FeatureTable.orgGuid eq orgId) and (FeatureTable.path eq it) }
                .singleOrNull()?.let { throw FeatureIsNotUnique() }
        }

        FeatureTable
            .updateAtMostOne(
                where = { (FeatureTable.orgGuid eq orgId) and (FeatureTable.guid eq featureId) },
                body = { it.updateFeature(update) }
            )
            .ifEq(0) { throw FeatureNotFound() }
        return@transaction checkNotNull(get(orgId, featureId))
    }

    private fun UpdateStatement.updateFeature(update: FeatureModel.Update) {
        update.name?.let { this[FeatureTable.name] = it }
        update.path?.let { this[FeatureTable.path] = it }
    }

    override fun delete(orgId: UUID, featureId: UUID) = transaction<Unit> {
        FeatureTable
            .deleteAtMostOneWhere { (FeatureTable.orgGuid eq orgId) and (FeatureTable.guid eq featureId) }
            .ifEq(0) { throw FeatureNotFound() }
    }
}
