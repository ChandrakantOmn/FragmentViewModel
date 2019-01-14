# FragmentViewModel
Passing Data between Android Fragments using ViewModel If your application uses fragments, one fragment shows list of items and another fragment displays details of the selected item, then list fragment needs to pass the selected item id to the item-details fragment so that it can fetch data for the item and display in UI.  This post shows with an example how to pass data between fragments using ViewModel and LiveData
Passing Data between Fragments by Defining Interface
The most common way to pass data between the fragments is by defining an interface with a method which is implemented by the container activity of list and item details fragments. The list fragment calls the method implemented by the activity and passes the data (item id) to it. The method in the activity finds the item-details fragment and sets the data received from item fragment by calling a setter method defined in the details fragment.

For details, you can see fragment to fragment communication by defining an interface.

Passing Data between Fragments using ViewModel
You can simplify and provide best solution using ViewModel and LiveData for passing data between two fragments of an activity. ViewModel has other advantages too such as separation of controllers from data handling and avoiding repeated data fetching due to configuration changes like screen rotation as view model is tied to activity lifecycle, for more information on view model see viewmodel tutorial.

To make passage of data from one fragment to another possible, we need to obtain view model object with activity scope in both fragments. Both fragments get the same view model instance which is tied to activity lifecycle. Item fragment passes item id to the set method of view model which sets the value on LiveData object. Details fragment listens to the live data object defined in the view model and capture the item id, fetches item details and displays it in UI.

Example
I’ll show how to implement communication between fragment using ViewModle and LiveData by taking players example. One fragment displays list of players in listview and second fragment shows details of the clicked player. On clicking a player in the list, live data object is updated with the selected player name. The second fragment which listens to live data for changes gets the selected player name and fetches and displays details in UI.

android passing data between fragments using viewmodel example
Setup
Add live data and view model dependency to build.gradle file.

def lifecycle_version = "1.1.1"
implementation "android.arch.lifecycle:extensions:$lifecycle_version"
Since we are using ToolBar, update styles.xml and use no action bar theme.

    <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
. . .
    </style>
Activity Layout
Main activity layout contains two fragments.

<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
    <android.support.v7.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorPrimary"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
    <fragment android:name="com.zoftino.players.PlayersListFragment"
        android:id="@+id/players_f"
        android:layout_weight="1"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/toolbar" />
    <fragment android:name="com.zoftino.players.PlayerDetailsFragment"
        android:id="@+id/player_details_f"
        android:layout_weight="2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/players_f"/>
</android.support.constraint.ConstraintLayout>
Activity
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        tb.setSubtitle("Tennis Players");
    }
}
List Fragment
List fragment gets an instance of view model using ViewModelProviders passing activity, fetches list of players and displays them in the list view. On-item-click listener calls a methods on view model to pass the selected player name to live data.

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayersListFragment extends Fragment {

    private PlayerViewModel viewModel;
    private ListView lv;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this.getActivity()).get(PlayerViewModel.class);

        lv.setAdapter(new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, viewModel.getPlayerList()));

        lv.setOnItemClickListener((adapter, itemView, pos, id) -> {
            TextView tv = (TextView)itemView;
            Toast.makeText(this.getContext(), tv.getText().toString(), Toast.LENGTH_SHORT).show();
            viewModel.selectPlayer(tv.getText().toString());
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.palyers,
                container, false);
        lv = (ListView)view.findViewById(R.id.players_lv);

        return view;
    }
}
List Fragment Layout
List fragment layout contains list view which displays list of players.

<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android" 
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <ListView android:id= "@+id/players_lv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"></ListView>
</android.support.constraint.ConstraintLayout>
Details Fragment
Details fragment gets the same view model instance which is used by the list fragment and adds change listener to live data object contained in it. The handler gets the selected player name, fetches the player details and displays in UI.

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PlayerDetailsFragment extends Fragment {
    private PlayerViewModel viewModel;

    private TextView name;
    private TextView age;
    private TextView country;
    private TextView titles;
    private TextView rank;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this.getActivity()).get(PlayerViewModel.class);

        viewModel.getSelectedPlayer().observe(this, item -> {
            displayDetails(viewModel.getPlayerDetails(item));
        });
    }

    public void displayDetails(Player player){
        name.setText(player.getName());
        age.setText(""+player.getAge());
        country.setText(player.getCountry());
        titles.setText(""+player.getTitles());
        rank.setText(""+player.getRank());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_details,
                container, false);

        name = view.findViewById(R.id.name);
        age = view.findViewById(R.id.age);
        country = view.findViewById(R.id.country);
        titles = view.findViewById(R.id.titles);
        rank = view.findViewById(R.id.rank);

        return view;
    }
}
Details Fragment Layout
Detail fragment layout contains text views used for showing player details.

<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_gravity="right">

    <TextView
        android:id="@+id/name_tv"
        android:layout_height="wrap_content"
        android:layout_width="100dp"
        android:layout_marginLeft="8dp"
        android:layout_marginTop="32dp"
        android:gravity="right"
        android:text="Name:"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toLeftOf="@+id/name"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/name"
        android:layout_width="250dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:textAppearance="@style/TextAppearance.AppCompat.Large"
        app:layout_constraintLeft_toRightOf="@+id/name_tv"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/age_tv"
        android:layout_width="100dp"
        android:layout_marginLeft="8dp"
        android:layout_marginTop="8dp"
        android:gravity="right"
        android:layout_height="wrap_content"
        android:text="Age:"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toLeftOf="@+id/age"
        app:layout_constraintTop_toBottomOf="@+id/name" />

    <TextView
        android:id="@+id/age"
        android:layout_width="250dp"
        android:layout_height="wrap_content"
        android:textAppearance="@style/TextAppearance.AppCompat.Large"
        app:layout_constraintLeft_toRightOf="@+id/age_tv"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/name" />

    <TextView
        android:id="@+id/country_tv"
        android:layout_width="100dp"
        android:layout_marginLeft="8dp"
        android:layout_marginTop="8dp"
        android:gravity="right"
        android:layout_height="wrap_content"
        android:text="Country:"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toLeftOf="@+id/country"
        app:layout_constraintTop_toBottomOf="@+id/age" />

    <TextView
        android:id="@+id/country"
        android:layout_width="250dp"
        android:layout_height="wrap_content"
        android:textAppearance="@style/TextAppearance.AppCompat.Large"
        app:layout_constraintLeft_toRightOf="@+id/country_tv"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/age" />

    <TextView
        android:id="@+id/titles_tv"
        android:layout_width="100dp"
        android:layout_marginLeft="8dp"
        android:layout_marginTop="8dp"
        android:gravity="right"
        android:layout_height="wrap_content"
        android:text="Titles:"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toLeftOf="@+id/titles"
        app:layout_constraintTop_toBottomOf="@+id/country" />

    <TextView
        android:id="@+id/titles"
        android:layout_width="250dp"
        android:layout_height="wrap_content"
        android:textAppearance="@style/TextAppearance.AppCompat.Large"
        app:layout_constraintLeft_toRightOf="@+id/titles_tv"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/country" />

    <TextView
        android:id="@+id/rank_tv"
        android:layout_width="100dp"
        android:layout_marginLeft="8dp"
        android:layout_marginTop="8dp"
        android:gravity="right"
        android:layout_height="wrap_content"
        android:text="Rank:"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toLeftOf="@+id/rank"
        app:layout_constraintTop_toBottomOf="@+id/titles" />

    <TextView
        android:id="@+id/rank"
        android:layout_width="250dp"
        android:layout_height="wrap_content"
        android:textAppearance="@style/TextAppearance.AppCompat.Large"
        app:layout_constraintLeft_toRightOf="@+id/rank_tv"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/titles" />
</android.support.constraint.ConstraintLayout>
ViewModel
View model contains live data object, method to set value on live data object and methods which provide player list and details.

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class PlayerViewModel extends ViewModel {
    private final MutableLiveData<String> selectedPlayer = new MutableLiveData<String>();

    private PlayersRepository repository = new PlayersRepository();

    public void selectPlayer(String playerName) {
        selectedPlayer.setValue(playerName);
    }

    public MutableLiveData<String> getSelectedPlayer() {
        return selectedPlayer;
    }

    public String[] getPlayerList(){
        return repository.getPlayers();
    }

    public Player getPlayerDetails(String name){
        return repository.getPlayerDetails(name);
    }
}
Repository
You can replace this repository class with something that fetches data from local db or remote source.

public class PlayersRepository {
    private String players[];
    private HashMap<String, Player> playerDetails;
    public String[] getPlayers(){
        if(players == null){
            players = new String[7];
            players[0] = "Rafael Nadal";
            players[1] = "Roger Federer";
            players[2] = "Juan Martin del Potro";
            players[3] = "Alexander Zverev";
            players[4] = "Grigor Dimitrov";
            players[5] = "Kevin Anderson";
            players[6] = "Marin Cilic";
        }

        return players;
    }

    public Player getPlayerDetails(String name){
        if(playerDetails == null){
            createPlayerDetailsMap();
        }
        return playerDetails.get(name);
    }

    public void createPlayerDetailsMap(){
        playerDetails = new HashMap<String, Player>();

        Player player = new Player();
        player.setName("Rafael Nadal");
        player.setAge(32);
        player.setCountry("Spain");
        player.setRank(1);
        player.setTitles(80);

        playerDetails.put("Rafael Nadal", player);

        player = new Player();
        player.setName("Roger Federer");
        player.setAge(37);
        player.setCountry("Switzerland");
        player.setRank(2);
        player.setTitles(98);

        playerDetails.put("Roger Federer", player);


        player = new Player();
        player.setName("Juan Martin del Potro");
        player.setAge(29);
        player.setCountry("Argentina");
        player.setRank(3);
        player.setTitles(22);

        playerDetails.put("Juan Martin del Potro", player);

        player = new Player();
        player.setName("Alexander Zverev");
        player.setAge(21);
        player.setCountry("Germany");
        player.setRank(4);
        player.setTitles(9);

        playerDetails.put("Alexander Zverev", player);

        player = new Player();
        player.setName("Grigor Dimitrov");
        player.setAge(27);
        player.setCountry("Bulgaria");
        player.setRank(5);
        player.setTitles(8);

        playerDetails.put("Grigor Dimitrov", player);

        player = new Player();
        player.setName("Kevin Anderson");
        player.setAge(32);
        player.setCountry("South Africa");
        player.setRank(6);
        player.setTitles(4);

        playerDetails.put("Kevin Anderson", player);
    }
}
Model
public class Player {

    private String name;
    private int age;
    private String country;
    private int rank;
    private int titles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getTitles() {
        return titles;
    }

    public void setTitles(int titles) {
        this.titles = titles;
    }
}
