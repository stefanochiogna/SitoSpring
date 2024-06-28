import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Disabled
@ExtendWith(MockitoExtension.class)
public class BaaseUnitTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private BaseService baseService;

    @BeforeEach
    public void setUp() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
    }

    @Test
    public void testUpdate() throws SQLException {
        Base base = new Base();
        base.setLocazione("Pisa");
        base.setMail("pisa@airforce.it");
        base.setTelefono("3256703544");
        base.setProvincia("Pisa");
        base.setCAP("56121");
        base.setVia("Aeronautica Pisa");
        base.setIdAdministrator("1234567890");
        base.setLatitudine(43.687882135016224);
        base.setLongitudine(10.394911976049698);
        //base.setFoto(foto);
        //base.setFotoDim(foto.length);

        baseService.update(base);

        // Verifica chiamate per la query di selezione
        verify(preparedStatement, times(1)).setString(1, base.getLocazione());
        verify(preparedStatement, times(1)).setString(2, base.getMail());
        verify(preparedStatement, times(1)).setString(3, base.getTelefono());
        verify(preparedStatement, times(1)).setString(4, base.getProvincia());
        verify(preparedStatement, times(1)).setString(5, base.getCAP());
        verify(preparedStatement, times(1)).setString(6, base.getVia());
        verify(preparedStatement, times(1)).setString(7, base.getIdAdministrator());
        verify(preparedStatement, times(1)).setDouble(8, base.getLatitudine());
        verify(preparedStatement, times(1)).setDouble(9, base.getLongitudine());
        verify(preparedStatement, times(1)).executeQuery();

        // Verifica chiamate per la query di aggiornamento
        verify(preparedStatement, times(1)).setString(1, base.getMail());
        verify(preparedStatement, times(1)).setString(2, base.getTelefono());
        verify(preparedStatement, times(1)).setString(3, base.getProvincia());
        verify(preparedStatement, times(1)).setString(4, base.getCAP());
        verify(preparedStatement, times(1)).setString(5, base.getVia());
        verify(preparedStatement, times(1)).setString(6, base.getIdAdministrator());
        verify(preparedStatement, times(1)).setDouble(7, base.getLatitudine());
        verify(preparedStatement, times(1)).setDouble(8, base.getLongitudine());

        // Verifica chiamata per il setBlob
        verify(preparedStatement, times(1)).setBlob(eq(9), any(ByteArrayInputStream.class), eq(foto.length));
        
        verify(preparedStatement, times(1)).setString(10, base.getLocazione());
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(2)).close();
        verify(resultSet, times(1)).close();
    }

    @Test
    public void testUpdate_existingBase() throws SQLException {
        when(resultSet.next()).thenReturn(true);

        
        Base base = new Base();
        base.setLocazione("Pisa");
        base.setMail("pisa@airforce.it");
        base.setTelefono("3256703544");
        base.setProvincia("Pisa");
        base.setCAP("56121");
        base.setVia("Aeronautica Pisa");
        base.setIdAdministrator("1234567890");
        base.setLatitudine(43.687882135016224);
        base.setLongitudine(10.394911976049698);
        //base.setFoto(foto);
        //base.setFotoDim(foto.length);
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> baseService.update(base));
        assertEquals("Aggiornamento base già esistente", exception.getMessage());

        verify(preparedStatement, times(1)).executeQuery();
        verify(resultSet, times(1)).close();
        verify(preparedStatement, times(1)).close();
    }
}