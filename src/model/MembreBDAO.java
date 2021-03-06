package model;

import DB.DBUtil;
import controller.AddController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.stage.Modality;

import java.sql.*;

/**
 * Created by Dell on 21/09/2016.
 */
public class MembreBDAO extends DAO<Membre, Bapteme> {

    private static final String AJOUTERMEMBRE = "INSERT INTO membre (nom,prenom,age,date_N,lieu_N" +
            ",phone,email,photo,sexe,adresse,profession,extras,situation_M) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String AJOUTERBAPTEME = "INSERT INTO bapteme(lieu_b,date_b,pasteur,eglise_d,eglise_pro,date_transfert,membre) VALUES (?,?,?,?,?,?,?)";

    private static final String MODIFIERMEMBRE = "UPDATE membre SET nom = ?,prenom = ?,date_N = ?,lieu_N = ?,age=?,phone = ?," +
            "email = ?,photo = ?,adresse = ?,profession = ?,extras = ? WHERE id = ? ";

    private static final String MODIFIERBAPTEME = "UPDATE bapteme SET lieu_b = ?,date_b = ?,pasteur = ?,eglise_d = ?" +
            ",eglise_pro = ?,date_transfert = ? WHERE id =?";

    private static final String LISTEMEMBRE = "SELECT id,nom,prenom,age,sexe,date_N,situation_M,adresse,profession," +
            "phone,email FROM membre WHERE status = ? ";


    private static final String INFOSMEMBREWITHCRITERIA = "SELECT id,nom,prenom,age,sexe,date_N,situation_M,adresse," +
            "profession,phone,email FROM membre WHERE status = ?";

    private static final String MEMBREDETAILS = "SELECT * FROM membre m JOIN bapteme b ON m.id = b.membre WHERE m.id =? ";

    private static final String DELETEMEMBRE = "DELETE FROM membre WHERE id = ?";

    private static final String CHANGESTATUS = "UPDATE membre SET status = ? WHERE id = ?";
    private ObservableList<Membre> membreList;

    public MembreBDAO(Connection con) throws SQLException, ClassNotFoundException {
        super(con);
        DAO.con = DBUtil.getConnexion();// TODO Auto-generated constructor stub
        if (!DBUtil.schemaExists(con)) {
            try {
                DBUtil.createSchema(con);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public Object[] getUnique(Membre membre, Bapteme bapteme) {
        try {
            PreparedStatement ps = con.prepareStatement(MEMBREDETAILS);
            ps.setInt(1, membre.getId());
           /* ps.setString(2, membre.getNom());
            ps.setString(3, membre.getPrenom());*/

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                membre.setId(rs.getInt("id"));
                membre.setNom(rs.getString("nom"));
                membre.setPrenom(rs.getString("prenom"));
                membre.setDateNaissance(rs.getDate("date_N").toLocalDate());
                membre.setLieuNaissance(rs.getString("lieu_N"));
                membre.setTelephone(rs.getString("phone"));
                membre.setEmail(rs.getString("email"));
                membre.setPhoto(rs.getBinaryStream("photo"));
                membre.setSexe(rs.getString("sexe"));
                membre.setAdresse(rs.getString("adresse"));
                membre.setProfession(rs.getString("profession"));
                membre.setExtra(rs.getString("extras"));
                membre.setSituationM(rs.getString("situation_M"));

              /*  membre = new Membre(rs.getString("nom"), rs.getString("prenom"), rs.getString("phone"),
                        rs.getString("sexe"), rs.getString("profession"), rs.getString("email"),
                        rs.getString("adresse"), rs.getBinaryStream("photo"), rs.getDate("date_N").toLocalDate(),
                        rs.getString("lieu_N"), rs.getString("extras"), rs.getString("situation_M"));*/

                bapteme = new Bapteme(rs.getDate("date_b").toLocalDate(), rs.getString("lieu_b"), rs.getString("pasteur"),
                        rs.getString("eglise_d"), rs.getString("eglise_pro"), rs.getDate("date_transfert").toLocalDate());


         /*       bapteme.setPasteur(rs.getString("pasteur"));
                bapteme.setLieuBapteme(rs.getString("lieu_b"));
                bapteme.setEgliseDest(rs.getString("eglise_d"));
                bapteme.setEglisePro(rs.getString("eglise_pro"));
                bapteme.setDateBapteme(rs.getDate("date_b").toLocalDate());
                bapteme.setDateTransfert(rs.getDate("date_transfert").toLocalDate());*/
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Object[]{membre, bapteme};
    }


    @Override
    public int update(Membre membre, Bapteme bapteme) {
        String query = "SELECT id FROM membre WHERE nom = " + "'" + membre.getNom() + "'" + " AND prenom = " + "'" + membre.getPrenom() + "'";
        int r = 0, r1 = 0;
        try {
           /* if (!DBUtil.schemaExists(con)) {
                try {
                    DBUtil.createSchema(con);
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            }*/

            PreparedStatement ps = con.prepareStatement(MODIFIERMEMBRE);
            ps.setString(1, membre.getNom());
            ps.setString(2, membre.getPrenom());
            ps.setDate(3, Date.valueOf(membre.getDateNaissance()));
            ps.setString(4, membre.getLieuNaissance());
            ps.setInt(5, AddController.calculateAge(membre.getDateNaissance()));
            ps.setString(6, membre.getTelephone());
            ps.setString(7, membre.getEmail());
            ps.setBinaryStream(8, membre.getPhoto());
            ps.setString(9, membre.getAdresse());
            ps.setString(10, membre.getProfession());
            ps.setString(11, membre.getExtra());
            ps.setInt(12, membre.getId());

            r = ps.executeUpdate();

            /*ResultSet rs = con.createStatement ().executeQuery (query);
            while (rs.next ()) {
                id = rs.getInt (1);
            }*/
            ps = con.prepareStatement(MODIFIERBAPTEME);
            ps.setString(1, bapteme.getLieuBapteme());
            ps.setDate(2, Date.valueOf(bapteme.getDateBapteme()));
            ps.setString(3, bapteme.getPasteur());
            ps.setString(4, bapteme.getEgliseDest());
            ps.setString(5, bapteme.getEglisePro());
            ps.setDate(6, Date.valueOf(bapteme.getDateTransfert()));
            ps.setInt(7, membre.getId());

            r1 = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.show();
        }

        return r + r1;
    }

    @Override
    public int delete(Membre membre) {
        int r = 0;
        try {
            PreparedStatement ps = con.prepareStatement(DELETEMEMBRE);
            ps.setInt(1, membre.getId());

            r = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r;
    }

    @Override
    public int add(Membre membre, Bapteme bapteme) {

        int id = 0, r = 0, r1 = 0;
        try {
           /* if (!DBUtil.schemaExists(con)) {
                try {
                    DBUtil.createSchema(con);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }*/
            PreparedStatement ps = con.prepareStatement(AJOUTERMEMBRE);
            ps.setString(1, membre.getNom());
            ps.setString(2, membre.getPrenom());
            ps.setInt(3, membre.getAge());
            ps.setDate(4, Date.valueOf(membre.getDateNaissance()));
            ps.setString(5, membre.getLieuNaissance());
            ps.setString(6, membre.getTelephone());
            ps.setString(7, membre.getEmail());
            ps.setBinaryStream(8, membre.getPhoto());
            ps.setString(9, membre.getSexe().substring(0, 1).toUpperCase());
            ps.setString(10, membre.getAdresse());
            ps.setString(11, membre.getProfession());
            ps.setString(12, membre.getExtra());
            ps.setString(13, membre.getSituationM());

            r = ps.executeUpdate();

            ResultSet rs = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE).executeQuery("SELECT id FROM membre WHERE nom = " + "'" + membre.getNom() + "'" + " AND prenom = " + "'" + membre.getPrenom() + "'");

            while (rs.next()) {
                id = rs.getInt(1);
            }

            ps = con.prepareStatement(AJOUTERBAPTEME);
            ps.setString(1, bapteme.getLieuBapteme());
            ps.setDate(2, Date.valueOf(bapteme.getDateBapteme()));
            ps.setString(3, bapteme.getPasteur());
            ps.setString(4, bapteme.getEgliseDest());
            ps.setString(5, bapteme.getEglisePro());
            ps.setDate(6, Date.valueOf(bapteme.getDateTransfert()));
            ps.setInt(7, id);

            r1 = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.show();
        }

        return r + r1;
    }

    @Override
    public ObservableList<Membre> getList(String status) {
        try {
            membreList = FXCollections.observableArrayList();
            PreparedStatement ps = con.prepareStatement(LISTEMEMBRE);
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
//            ResultSet rs = con.createStatement ().executeQuery (INFOSMEMBRE);
            while (rs.next()) {
                Membre membre = new Membre();
                membre.setId(rs.getInt(1));
                membre.setNom(rs.getString(2));
                membre.setPrenom(rs.getString(3));
                membre.setAge(rs.getInt(4));
                membre.setSexe(rs.getString(5).substring(0, 1).toUpperCase());
                membre.setDateNaissance(rs.getDate(6).toLocalDate());
                membre.setSituationM(rs.getString(7));
                membre.setAdresse(rs.getString(8));
                membre.setProfession(rs.getString(9));
                membre.setTelephone(rs.getString(10));
                membre.setEmail(rs.getString(11));
                membreList.add(membre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return membreList;
    }

  /*  public ObservableList<Membre> getMembreInactiveList() {
        try {
            membreList = FXCollections.observableArrayList ();
            ResultSet rs = con.createStatement ().executeQuery (INFOSMEMBREINACTIVE);
            while (rs.next ()) {
                Membre membre = new Membre ();
                membre.setId (rs.getInt (1));
                membre.setNom (rs.getString (2));
                membre.setPrenom (rs.getString (3));
                membre.setSexe (rs.getString (4).substring (0, 1).toUpperCase ());
                membre.setDateNaissance (rs.getDate (5).toLocalDate ());
                membre.setSituationM (rs.getString (6));
                membre.setAdresse (rs.getString (7));
                membre.setProfession (rs.getString (8));
                membre.setTelephone (rs.getString (9));
                membre.setEmail (rs.getString (10));
                membreList.add (membre);
            }
        } catch (SQLException e) {
            e.printStackTrace ();
        }
        return membreList;
    }*/

    @Override
    public int changeStatus(Membre membre, String status) {
        int i = 0;
        try {
            PreparedStatement ps = con.prepareStatement(CHANGESTATUS);
            ps.setString(1, status);
            ps.setInt(2, membre.getId());

            i = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public ObservableList<Membre> getMembreListWithCriteria(String status, String criteria) throws SQLException {
        ObservableList<Membre> membreList = FXCollections.observableArrayList();
//        ResultSet rs = con.createStatement ().executeQuery (INFOSMEMBREWITHCRITERIA + " " + status + " ORDER BY " + criteria);
        PreparedStatement ps = con.prepareStatement(INFOSMEMBREWITHCRITERIA + "ORDER BY " + criteria);
        ps.setString(1, status);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Membre membre = new Membre();
            membre.setId(rs.getInt(1));
            membre.setNom(rs.getString(2));
            membre.setPrenom(rs.getString(3));
            membre.setAge(rs.getInt(4));
            membre.setSexe(rs.getString(5).substring(0, 1).toUpperCase());
            membre.setDateNaissance(rs.getDate(6).toLocalDate());
            membre.setSituationM(rs.getString(7));
            membre.setAdresse(rs.getString(8));
            membre.setProfession(rs.getString(9));
            membre.setTelephone(rs.getString(10));
            membre.setEmail(rs.getString(11));
            membreList.add(membre);
        }
        return membreList;
    }

    public ObservableList<PieChart.Data> getPieChartData(String stats) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        String labelEnfants = "ENFANTS", labelVieux = "VIEUX", labelJeunes = "JEUNES", labelAdultes = "ADULTES";
        String query1 = " SELECT sum(ENFANTS) AS ENFANTS FROM( SELECT COUNT(*) AS ENFANTS,date_n,age FROM membre GROUP BY date_n  HAVING age BETWEEN 0 AND 14) AS mediator  ;";
        String query2 = " SELECT sum(JEUNES) AS JEUNES FROM( SELECT COUNT(*) AS JEUNES,date_n,age FROM membre GROUP BY date_n  HAVING age BETWEEN 15 AND 30) AS mediator  ;";
        String query3 = " SELECT sum(ADULTES) AS ADULTES FROM( SELECT COUNT(*) AS ADULTES,date_n,age FROM membre GROUP BY date_n  HAVING age BETWEEN 31 AND 59) AS mediator  ;";
        String query4 = " SELECT sum(VIEUX) AS VIEUX FROM( SELECT COUNT(*) AS VIEUX,date_n,age FROM membre GROUP BY date_n  HAVING age > 60) AS mediator  ;";
//        String query5 = "SELECT count(*) ,situation_m FROM membre  GROUP BY situation_m";
        String query6 = "SELECT count(*) ,sexe FROM membre GROUP BY sexe";


        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = con.createStatement();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        switch (stats) {
            case "AGE":
                try {
                    if (statement != null) {
                        rs = statement.executeQuery(query1);
                    }
                    assert rs != null;
                    while (rs.next()) {
                        data.add(new PieChart.Data(rs.getString(1), (double) rs.getInt(1)));
                    }
                    rs = statement.executeQuery(query2);
                    while (rs.next()) {
                        data.add(new PieChart.Data(labelJeunes, (double) rs.getInt(1)));
                    }
                    rs = statement.executeQuery(query3);
                    while (rs.next()) {
                        data.add(new PieChart.Data(labelAdultes, (double) rs.getInt(1)));
                    }
                    rs = statement.executeQuery(query4);
                    while (rs.next()) {
                        data.add(new PieChart.Data(labelVieux, (double) rs.getInt(1)));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
          /*  case "MARITAL":
                try {
                    rs = statement != null ? statement.executeQuery(query5) : null;
                    int i = 0;
                    while (rs != null && rs.next()) {
                        data.add(new PieChart.Data(rs.getString(2).toUpperCase() + "S", (double) rs.getInt(1)));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return datas;
                break;*/
            case "SEXE":
                try {
                    rs = statement != null ? statement.executeQuery(query6) : null;
                    int i = 0;
                    assert rs != null;
                    while (rs.next()) {
                        data.add(new PieChart.Data(rs.getString(2).toUpperCase(), (double) rs.getInt(1)));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            default:
                data = null;
        }

        return data;
    }

    public ObservableList<XYChart.Series<String, Integer>> getChartData(String stats) {
        String labelEnfants = "ENFANTS", labelVieux = "VIEUX", labelJeunes = "JEUNES", labelAdultes = "ADULTES";
        String query1 = " SELECT sum(ENFANTS) AS ENFANTS FROM( SELECT COUNT(*) AS ENFANTS,date_n,age FROM membre GROUP BY date_n  HAVING age BETWEEN 0 AND 14) AS mediator  ;";
        String query2 = " SELECT sum(JEUNES) AS JEUNES FROM( SELECT COUNT(*) AS JEUNES,date_n,age FROM membre GROUP BY date_n  HAVING age BETWEEN 15 AND 30) AS mediator  ;";
        String query3 = " SELECT sum(ADULTES) AS ADULTES FROM( SELECT COUNT(*) AS ADULTES,date_n,age FROM membre GROUP BY date_n  HAVING age BETWEEN 31 AND 59) AS mediator  ;";
        String query4 = " SELECT sum(VIEUX) AS VIEUX FROM( SELECT COUNT(*) AS VIEUX,date_n,age FROM membre GROUP BY date_n  HAVING age > 60) AS mediator  ;";

        String query5 = "SELECT count(*) ,situation_m FROM membre  GROUP BY situation_m";
        String query6 = "SELECT count(*) ,sexe FROM membre GROUP BY sexe";
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = con.createStatement();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ObservableList<XYChart.Series<String, Integer>> data = FXCollections.observableArrayList();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
        XYChart.Series<String, Integer> series2 = new XYChart.Series<>();
        XYChart.Series<String, Integer> series3 = new XYChart.Series<>();
        XYChart.Series<String, Integer> series4 = new XYChart.Series<>();
//        XYChart.Series<String, Integer> series5 = new XYChart.Series<>();

        switch (stats) {
            case "MARITAL":
                try {
                    if (statement != null) {
                        rs = statement.executeQuery(query5);
                    }
                    while (rs != null && rs.next()) {
                        series.getData().add(new XYChart.Data<>(rs.getString(2).toUpperCase() + "S", rs.getInt(1)));
                    }
                    data.add(series);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                break;
            case "SEXE":
                try {
                    rs = statement != null ? statement.executeQuery(query6) : null;
                    assert rs != null;
                    while (rs.next()) {
                        series.getData().add(new XYChart.Data<>(rs.getString(2).toUpperCase(), rs.getInt(1)));
                    }
                    data.add(series);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "AGE":
                try {
                    rs = statement.executeQuery(query1);
                    assert rs != null;
                    series1.setName("ENFANTS");
                    while (rs.next()) {
                        series1.getData().add(new XYChart.Data<>(labelEnfants, rs.getInt(1)));
                    }
                    rs = statement.executeQuery(query2);
                    series2.setName("JEUNES");
                    while (rs.next()) {
                        series2.getData().add(new XYChart.Data<>(labelJeunes, rs.getInt(1)));
                    }
                    series3.setName("ADULTES");
                    rs = statement.executeQuery(query3);
                    while (rs.next()) {
                        series3.getData().add(new XYChart.Data<>(labelAdultes + " ESSAI PRIMORDIAL", rs.getInt(1)));

                    }
                    rs = statement.executeQuery(query4);
                    series4.setName("VIEUX");
                    while (rs.next()) {
                        series4.getData().add(new XYChart.Data<String, Integer>(labelVieux, rs.getInt(1)));
                    }
                    data.addAll(series1, series2, series3, series4);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            default:
                data = null;
        }

        return data;
    }

    public ObservableList<Membre> getExtrema() {
//        String max = "SELECT MAX(age) AS vieux,nom,prenom FROM (SELECT id,nom,prenom,date_n,age FROM membre GROUP BY nom) AS mediator";
        String max = "SELECT nom,prenom,age FROM membre WHERE  age = (SELECT max(age) FROM membre)";
        String min = "SELECT nom,prenom,age FROM membre WHERE  age = (SELECT min(age) FROM membre)";
//        String min = "SELECT MIN(age) AS jeune,nom,prenom FROM (SELECT id,nom,prenom,date_n,age FROM membre GROUP BY nom) AS mediator";

        ObservableList<Membre> membres = FXCollections.observableArrayList();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = con.createStatement();
            rs = st.executeQuery(max);
            while (rs.next()) {
                Membre membre = new Membre();
                membre.setPrenom(rs.getString("prenom"));
                membre.setNom(rs.getString("nom"));
                membre.setAge(Integer.valueOf(rs.getString("age")));
                membres.add(membre);
            }

            rs = st.executeQuery(min);
            while (rs.next()) {
                Membre membre = new Membre();
                membre.setNom(rs.getString("nom"));
                membre.setPrenom(rs.getString("prenom"));
                membre.setAge(Integer.valueOf(rs.getString("age")));
                membres.add(membre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return membres;
    }

    public ObservableList<Membre> populateChoiceBox() {
        ObservableList<Membre> membres = FXCollections.observableArrayList();
        Statement st = null;
        ResultSet rs = null;
        String query = "SELECT id,nom,prenom FROM membre";

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                Membre membre = new Membre();
                membre.setId(rs.getInt(1));
                membre.setNom(rs.getString(2));
                membre.setPrenom(rs.getString(3));
                membres.add(membre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return membres;

    }
}
