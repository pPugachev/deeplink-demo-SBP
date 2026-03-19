package ru.demo.raiffeisen.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BankAdapter(
    private var members: List<Member>,
    private val onItemClick: (Member) -> Unit
) : RecyclerView.Adapter<BankAdapter.BankViewHolder>(), Filterable {

    private var filteredList: List<Member> = members

    class BankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView = itemView.findViewById(R.id.ivLogo)
        val name: TextView = itemView.findViewById(R.id.tvName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bank, parent, false)
        return BankViewHolder(view)
    }

    override fun onBindViewHolder(holder: BankViewHolder, position: Int) {
        val member = filteredList[position]
        holder.name.text = member.name
        if (!member.logo.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(member.logo)
                .into(holder.logo)
        }
        holder.itemView.setOnClickListener { onItemClick(member) }
    }

    override fun getItemCount(): Int = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase() ?: ""
                filteredList = if (query.isEmpty()) {
                    members
                } else {
                    members.filter { it.name.lowercase().contains(query) }
                }
                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<Member> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    fun updateData(newMembers: List<Member>) {
        members = newMembers
        filteredList = newMembers
        notifyDataSetChanged()
    }
}