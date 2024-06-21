@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SitoForzeArmateApplication.class })
@WebAppConfiguration
public class PaginaInizialeIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    /* @Test
    public void givenWac_whenServletContext_thenItProvidesPaginaInizialeController() {
        ServletContext servletContext = webApplicationContext.getServletContext();
    
        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean("")); 
    } */ 

    @Test
    public void givenInitURI_whenMockMVC_thenReturnsindexJSPViewName() {
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(view().name("index"));
    }

    @Test
    public void givenHomepageURI_whenMockMVC_thenReturnsindexJSPViewName() {
        this.mockMvc.perform(get("/homepage")).andDo(print()).andExpect(view().name("Pagina_InizialeCSS"));
    }
}