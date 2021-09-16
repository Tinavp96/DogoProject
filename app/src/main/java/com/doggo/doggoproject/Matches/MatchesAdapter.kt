package com.doggo.doggoproject.Matches

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.doggo.doggoproject.Matches.MatchesObject
import com.doggo.doggoproject.Matches.MatchesViewHolders
import com.doggo.doggoproject.R

class MatchesAdapter(private val matchesList: List<MatchesObject>, private val context: Context) : RecyclerView.Adapter<MatchesViewHolders>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolders {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_matches, null, false)
        val lp = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutView.layoutParams = lp
        return MatchesViewHolders(layoutView)
    }

    override fun onBindViewHolder(holder: MatchesViewHolders, position: Int) {
        holder.mMatchId.text = matchesList[position].userId
        holder.mMatchName.text = matchesList[position].name
        if (matchesList[position].profileImageUrl != "default") {
            Glide.with(context).load(matchesList[position].profileImageUrl).into(holder.mMatchImage)
        }
    }

    override fun getItemCount(): Int {
        return matchesList.size
    }

}