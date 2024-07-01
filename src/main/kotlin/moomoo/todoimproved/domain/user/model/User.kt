package moomoo.todoimproved.domain.user.model

import jakarta.persistence.*
import moomoo.todoimproved.domain.common.BaseEntity

@Entity
@Table(name = "app_user")
class User(
    var nickname: String,

    var password: String,

    var name: String,

    var profileImageUrl: String? = null
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun checkPermission(userId: Long): Boolean {
        return id == userId
    }

    fun updateProfileImage(imageUrl: String) {
        profileImageUrl = imageUrl
    }
}