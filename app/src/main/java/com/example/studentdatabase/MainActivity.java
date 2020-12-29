package com.example.studentdatabase;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    String Hoten[] = {"Eduria","Chris Abad","Tuto","support","Matt from Ionic","Udemy Instructor",
            "GitHub","Google","Entopy","Dabia","Jesus","Gawr Gura"};
    String Email[] = {"Eduria@gmal.com","ChrisAbad@na.com","Tuto@ka.com","support@lame.com",
            "MattfromIonic@kl.com","UdemyInstructor@koa.com",
            "GitHub@sh.com","Google@haha.com","Entopy@plo.com","Dabia@plo.com","Jesus@plo.com",
            "Gawr Gura@plo.com"};

    SQLiteDatabase db;
    Cursor curs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        String dataPath = getFilesDir() + "/studentDB";
        db = SQLiteDatabase.openDatabase(dataPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        generateDB();
        curs = db.rawQuery("select * from sinhvien",
                null);
        ListView listView = findViewById(R.id.list_students);
        listView.setAdapter(new StudentAdapter(curs));
        listView.setOnItemClickListener(this);
    }

    public void reset(){
        setContentView(R.layout.activity_student_list);

        String dataPath = getFilesDir() + "/studentDB";
        db = SQLiteDatabase.openDatabase(dataPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        curs = db.rawQuery("select * from sinhvien",
                null);
        ListView listView = findViewById(R.id.list_students);
        listView.setAdapter(new StudentAdapter(curs));
        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_bar,menu);

        MenuItem add =menu.findItem(R.id.add);
        add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                setContentView(R.layout.add_student);

                EditText hoten = findViewById(R.id.nHoten);
                EditText mssv = findViewById(R.id.nMSSV);
                EditText email = findViewById(R.id.nemail);
                EditText diachi = findViewById(R.id.naddr);
                EditText ngaysinh = findViewById(R.id.nngaysinh);

                findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.beginTransaction();
                        try{
                                String sql = String.format("insert into sinhvien(mssv, hoten, ngaysinh, email, diachi) " +
                                        "values('%s', '%s', '%s', '%s', '%s')", mssv.getText(), hoten.getText(),
                                        ngaysinh.getText(), email.getText(), diachi.getText());
                                db.execSQL(sql);
                            db.setTransactionSuccessful();
                        }catch(Exception e){
                            e.printStackTrace();
                        }finally {
                            db.endTransaction();
                        }
                        reset();
                    }
                });
                findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reset();
                    }
                });
                return false;
            }

        });
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.v("TAG", "Search with keyword: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                curs = db.rawQuery("select * from sinhvien" +" where"
                                + " upper(hoten) like \"%"+newText.toUpperCase()
                                + "%\"" + " OR"+" mssv like \"%" + newText.toUpperCase() + "%\"",
                        null);
                reset();
                Log.v("TAG", "Keyword: " + newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void generateDB(){
        db.beginTransaction();
        try{
            db.execSQL("drop table sinhvien");
            db.execSQL("create table sinhvien(" +
                    "mssv char(8) primary key," +
                    "hoten text," +
                    "ngaysinh date," +
                    "email text," +
                    "diachi text);");

            for (int i = 0; i < 50; i++) {
                String mssv = "2017" + (1000+i);
                String hoten = Hoten[i%12];
                String ngaysinh = "08/05/20"+(10+i);
                String email = Email[i%12];
                String diachi = "So " + i +" Ta Quang Buu";
                String sql = String.format("insert into sinhvien(mssv, hoten, ngaysinh, email, diachi) " +
                        "values('%s', '%s', '%s', '%s', '%s')", mssv, hoten, ngaysinh, email, diachi);

                db.execSQL(sql);
            }

            db.setTransactionSuccessful();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }

    @Override
    protected void onStop() {
        db.close();
        super.onStop();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = db.rawQuery("select * from sinhvien",
                null);
        cursor.moveToPosition(position);
        setContentView(R.layout.display_student);

        TextView name = findViewById(R.id.Hoten);
        TextView mssv = findViewById(R.id.MSSV);
        TextView email = findViewById(R.id.email);
        TextView ngaysinh = findViewById(R.id.dob);
        TextView diachi = findViewById(R.id.addr);

        name.setText(cursor.getString(cursor.getColumnIndex("hoten")));
        mssv.setText(cursor.getString(cursor.getColumnIndex("mssv")));
        email.setText(cursor.getString(cursor.getColumnIndex("email")));
        ngaysinh.setText(cursor.getString(cursor.getColumnIndex("ngaysinh")));
        diachi.setText(cursor.getString(cursor.getColumnIndex("diachi")));

        Button ok = findViewById(R.id.back);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }
}

class StudentAdapter extends BaseAdapter {

    Cursor cs;

    public StudentAdapter(Cursor cursor) {
        cs = cursor;
    }

    @Override
    public int getCount() {
        return cs.getCount();
    }

    @Override
    public Object getItem(int i) {
        cs.moveToPosition(i);
        return cs;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item, viewGroup,
                    false);
        }

        TextView textMSSV = view.findViewById(R.id.text_mssv);
        TextView textHoten = view.findViewById(R.id.text_hoten);
        TextView textEmail = view.findViewById(R.id.text_email);

        cs.moveToPosition(i);

        textMSSV.setText(cs.getString(cs.getColumnIndex("mssv")));
        textHoten.setText(cs.getString(cs.getColumnIndex("hoten")));
        textEmail.setText(cs.getString(cs.getColumnIndex("email")));

        return view;
    }


}