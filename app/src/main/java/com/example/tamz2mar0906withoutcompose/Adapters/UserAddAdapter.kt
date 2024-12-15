package com.example.tamz2mar0906withoutcompose.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tamz2mar0906withoutcompose.Http.GroupResponseObject
import com.example.tamz2mar0906withoutcompose.Http.UserInfoResponseObject
import com.example.tamz2mar0906withoutcompose.R
import com.example.tamz2mar0906withoutcompose.Services.GroupService
import com.example.tamz2mar0906withoutcompose.Utilities.showCustomSnackbar
import android.util.Base64
import android.graphics.BitmapFactory

class UserAddAdapter(
    private val context: Context,
    private val group: GroupResponseObject,
    private val users: MutableList<UserInfoResponseObject>,
    private val onUserAdded: (UserInfoResponseObject) -> Unit
) : RecyclerView.Adapter<UserAddAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userNameTextView: TextView = view.findViewById(R.id.userNameTextView)
        val userPhotoImageView: ImageView = view.findViewById(R.id.userPhotoImageView)
        val addUserButton: Button = view.findViewById(R.id.addUserButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        holder.userNameTextView.text = user.loginName
        user.photo?.let {
            val decodedBytes = Base64.decode(it, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            holder.userPhotoImageView.setImageBitmap(bitmap)
        }

        holder.addUserButton.setOnClickListener {
            GroupService.insertUserToGroup(context, holder.itemView, group.id.toString(), user.id.toString()) { success ->
                if (success) {
                    onUserAdded(user)
                    removeUserAt(position)
                }
            }
        }
    }

    override fun getItemCount(): Int = users.size

    private fun removeUserAt(position: Int) {
        users.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, users.size)
    }
}
