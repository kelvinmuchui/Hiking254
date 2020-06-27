package com.kelvin.notekeeper.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;
import com.kelvin.notekeeper.Models.ModelPost;
import com.kelvin.notekeeper.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {


    Context context;
    List<ModelPost>postList;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts,parent,false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int i) {
        final String uid = postList.get(i).getUid();
        String uEmail = postList.get(i).getuEmail();
        String uName = postList.get(i).getuName();
        String uDp = postList.get(i).getuDp();
        String pId = postList.get(i).getpId();
        String pTitle = postList.get(i).getpTitle();
        String pDescr = postList.get(i).getpDescr();
        String pImage = postList.get(i).getpImage();
        String pTimeStamp= postList.get(i).getpTime();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();


        //set data
        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.pTitle.setText(pTitle);
        holder.pDescTv.setText(pDescr);
        //set dp
        try{

            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_img_white).into(holder.uPictureIv);

        }catch (Exception e){

        }

        //set post image

        if (pImage.equals("noImage")){

            holder.pImageIv.setVisibility(View.GONE);

        }else{
            try{

                Picasso.get().load(pImage).into(holder.pImageIv);

            }catch (Exception e){

            }
        }


        //handle clicks
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "More", Toast.LENGTH_SHORT).show();
            }
        });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
            }
        });
        holder.comentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
            }
        });

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
            }
        });
        holder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(context, ThereProfileActivity.class);
//                intent.putExtra("uid",uid);
//                context.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //View Holder Class
    class  MyHolder extends RecyclerView.ViewHolder{





            ImageView uPictureIv, pImageIv;
            TextView uNameTv,pTimeTv,pDescTv,pLikesTv,pTitle;
            ImageButton moreBtn;
            Button likeBtn, comentBtn,shareBtn;
            LinearLayout profileLayout;
        public MyHolder(@NonNull View itemView) {
            super(itemView);


            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv =itemView.findViewById(R.id.pImageIv);
            uNameTv =itemView.findViewById(R.id.uNameTv);
            pTimeTv =itemView.findViewById(R.id.uTimeTv);
            pTitle =itemView.findViewById(R.id.pTitleTv);
            pDescTv =itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv =itemView.findViewById(R.id.pLikesTv);
            likeBtn =itemView.findViewById(R.id.likeBtn);
            moreBtn =itemView.findViewById(R.id.moreBtn);
            comentBtn =itemView.findViewById(R.id.commentBtn);
            shareBtn =itemView.findViewById(R.id.shareBtn);
            profileLayout =itemView.findViewById(R.id.profileLayout);



        }
    }
}
