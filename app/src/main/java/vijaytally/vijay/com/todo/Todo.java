package vijaytally.vijay.com.todo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Todo extends Activity {

    EditText add_note_EditText;
    TextView add_TextView;
    RecyclerView notes_List_RecyclerView;
    NotesListAdapter notesListAdapter;
    ArrayList<Noteslist> noteslists=new ArrayList<>();
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        add_note_EditText = (EditText)findViewById(R.id.add_note_EditText);
        add_TextView = (TextView)findViewById(R.id.add_TextView);
        notes_List_RecyclerView = (RecyclerView)findViewById(R.id.notes_List_RecyclerView);

        db = openOrCreateDatabase("note.db", MODE_APPEND, null);
        db.execSQL("Create table if not exists notes(id integer primary key autoincrement,note VARCHAR);");
        Cursor c = db.rawQuery("select * from notes",null);
        if (c.moveToFirst()) {
            do {
                Noteslist notelist = new Noteslist();
                notelist.setId(c.getInt(c.getColumnIndex("id")));
                notelist.setName(c.getString(c.getColumnIndex("note")));
                noteslists.add(notelist);
            } while (c.moveToNext());
        }

        notesListAdapter = new NotesListAdapter(this, noteslists);
        notesListAdapter.notifyDataSetChanged();
        notes_List_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notes_List_RecyclerView.setAdapter(notesListAdapter);
        notes_List_RecyclerView.setHasFixedSize(true);

        add_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add_note_EditText.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "empty", Toast.LENGTH_LONG).show();
                } else {
                    db.execSQL("insert into notes (note) values('" + add_note_EditText.getText().toString() + "');");
                    add_note_EditText.setText("");
                    recreate();
                }
            }
        });
    }

    private class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.ViewHolder>  {
        ArrayList<Noteslist> noteslist;
        Context context;

        public NotesListAdapter(Todo todo, ArrayList<Noteslist> noteslists) {
            this.context=todo;
            this.noteslist=noteslists;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notes_list, null);
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            viewHolder.notes_TextView.setText(noteslist.get(i).getName());
            final int _id=noteslist.get(i).getId();
            viewHolder.edit_TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(Todo.this);
                    dialog.setContentView(R.layout.activity_modify_record);
                    dialog.setTitle("Update notes");
                    final EditText note_EditText = (EditText) dialog.findViewById(R.id.update_notes_edittext);
                    dialog.show();
                    Button declineButton = (Button) dialog.findViewById(R.id.btn_update);
                    declineButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            db.execSQL("update notes SET note='" + note_EditText.getText().toString() + "' where id='" + _id + "'");
                            recreate();
                            dialog.dismiss();
                        }
                    });
                }
            });

            viewHolder.delete_TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.execSQL("DELETE FROM notes where id='"+_id+"'");
                    recreate();
                }
            });

            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (viewHolder.checkBox.isChecked()){
                        viewHolder.notes_TextView.setPaintFlags(viewHolder.notes_TextView.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                    }else {
                        viewHolder.notes_TextView.setPaintFlags(viewHolder.notes_TextView.getCurrentTextColor()|Paint.FAKE_BOLD_TEXT_FLAG);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return noteslist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            CheckBox checkBox;
            TextView notes_TextView,edit_TextView,delete_TextView;
            private AdapterView.OnItemClickListener onItemClickListener;
            private ItemClickListener clickListener;
            public ViewHolder(View itemView) {
                super(itemView);
                checkBox = (CheckBox)itemView.findViewById(R.id.Select_CheckBox);
                notes_TextView = (TextView)itemView.findViewById(R.id.notes_TextView);
                edit_TextView = (TextView)itemView.findViewById(R.id.edit_TextView);
                delete_TextView = (TextView)itemView.findViewById(R.id.delete_TextView);
            }
            public void setClickListener(ItemClickListener itemClickListener) {
                this.clickListener = itemClickListener;
            }
            @Override
            public void onClick(View view) {
                clickListener.onClick(view,getPosition(),false);
            }
        }
    }
}
