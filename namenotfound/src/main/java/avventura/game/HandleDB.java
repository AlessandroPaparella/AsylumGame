package game;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import engine.GameDescription;

public class HandleDB {
	Connection conn;
	public HandleDB() throws SQLException, Exception {
		Class.forName("org.h2.Driver");
		conn = DriverManager.getConnection("jdbc:h2:~/saves");
		final String elimina = "DROP TABLE saves";

		final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS saves (player VARCHAR(30) PRIMARY KEY,"
				+ " day VARCHAR(11),"
				+ " game OBJECT )";
		Statement stm = conn.createStatement();
		stm.executeUpdate(elimina);
		stm.executeUpdate(CREATE_TABLE);
		stm.close();
	}

	public void insertionTuple(String player, Object obj) throws SQLException {
		PreparedStatement pstm = conn.prepareStatement(
				"INSERT INTO saves "
				+ "VALUES (?, ?, ?)");
		pstm.setString(1, player);
		pstm.setString(2, LocalDate.now().toString());
		pstm.setObject(3, obj);

		pstm.executeUpdate();
		pstm.close();
	}

	public void deleteTuple(String player) throws SQLException {
		PreparedStatement pstm = conn.prepareStatement(
				"DELETE FROM saves WHERE player = ?");
		pstm.setString(1, player);

		pstm.executeUpdate();
		pstm.close();
	}

	public void updateTuple(String player, Object obj) throws SQLException {
		PreparedStatement pstm = conn.prepareStatement(
				"UPDATE saves "
				+ "SET day = ?, game = ? WHERE player = ?");
		pstm.setString(1, LocalDate.now().toString());
		pstm.setObject(2, obj);
		pstm.setString(3, player);
		pstm.executeUpdate();
		pstm.close();
	}

	public Map<String,GameDescription> recoveryTuple() throws SQLException, Exception{
		Map<String,GameDescription> tuple = new HashMap<>();

		Statement stm = conn.createStatement();
		ResultSet rs = stm.executeQuery("SELECT *"
									+ "FROM saves");
		while (rs.next()) {
			byte[] buf = rs.getBytes(3);
			ObjectInputStream objectIn = null;
			if (buf != null) objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));

			Object deSerializedObject = objectIn.readObject();

			tuple.put(rs.getString(1).concat(" ").concat(rs.getString(2)), (GameDescription) deSerializedObject);
		}
		rs.close();
		stm.close();

		return tuple;
	}

	public void closeConnection() throws Exception{
		conn.close();
	}

}
