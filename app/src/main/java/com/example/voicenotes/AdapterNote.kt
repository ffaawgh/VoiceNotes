package com.example.voicenotes

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.voicenotes.databinding.NotesBinding

class AdapterNote: ListAdapter<Note, AdapterNote.ItemHolder>(ItemComparator()) {

    class ItemHolder (private val binding: NotesBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (note: Note) = with(binding){
            //idNotes.text = note.id_note
            dateNotes.text = note.date_note
            userNotes.text = note.user_note
            nameNotes.text = note.name_note
            textNotes.text = note.text_note
       }
       companion object{
        fun create(parent: ViewGroup): ItemHolder{
            return ItemHolder(
                NotesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }
}

    class ItemComparator : DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

