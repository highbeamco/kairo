package io.limberapp.backend.module.orgs.store.org

import com.google.inject.Inject
import com.mongodb.client.MongoDatabase
import io.limberapp.backend.module.orgs.entity.org.MembershipEntity
import io.limberapp.backend.module.orgs.entity.org.OrgEntity
import io.limberapp.framework.store.MongoCollection
import io.limberapp.framework.store.MongoStore
import org.litote.kmongo.and
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.ne
import org.litote.kmongo.pullByFilter
import org.litote.kmongo.push
import java.util.UUID

internal class MongoOrgStore @Inject constructor(
    mongoDatabase: MongoDatabase
) : OrgStore, MongoStore<OrgEntity, OrgEntity.Update>(
    collection = MongoCollection(
        mongoDatabase = mongoDatabase,
        collectionName = OrgEntity.collectionName,
        clazz = OrgEntity::class
    )
) {

    override fun getByMemberId(memberId: UUID) =
        collection.find(OrgEntity::members / MembershipEntity::userId eq memberId)

    override fun createMembership(id: UUID, entity: MembershipEntity): Unit? {
        return collection.findOneAndUpdate(
            filter = and(
                OrgEntity::id eq id,
                OrgEntity::members / MembershipEntity::userId ne entity.userId
            ),
            update = push(OrgEntity::members, entity)
        )?.let { Unit }
    }

    override fun deleteMembership(id: UUID, memberId: UUID): Unit? {
        return collection.findOneAndUpdate(
            filter = and(
                OrgEntity::id eq id,
                OrgEntity::members / MembershipEntity::userId eq memberId
            ),
            update = pullByFilter(OrgEntity::members, MembershipEntity::userId eq memberId)
        )?.let { Unit }
    }
}
