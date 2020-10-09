import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class URLResponseInit {

    static final File WEB_ROOT = new File(".");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "not_supported.html";
    static final String REDIRECT_RECORDED = "redirect_recorded.html";
    static final String REDIRECT = "redirect.html";
    static final String NOT_FOUND = "notfound.html";

    private byte[] RedirectPage;
    private byte[] RedirectRecordedPage;
    private byte[] NotFoundPage;

    private int RedirectPageLength;
    private int RedirectRecordedPageLength;
    private int NotFoundPageLength;

    public URLResponseInit() {
        try {
            this.initFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNotFoundPageLength() {
        return NotFoundPageLength;
    }

    public int getRedirectRecordedPageLength() {
        return RedirectRecordedPageLength;
    }

    public int getRedirectPageLength() {
        return RedirectPageLength;
    }

    public byte[] getNotFoundPage() {
        return NotFoundPage;
    }

    private void setNotFoundPage(byte[] notFoundPage) {
        this.NotFoundPage = notFoundPage;
    }

    public byte[] getRedirectRecordedPage() {
        return RedirectRecordedPage;
    }

    private void setRedirectRecordedPage(byte[] redirectRecordedPage) {
        this.RedirectRecordedPage = redirectRecordedPage;
    }

    public byte[] getRedirectPage() {
        return RedirectPage;
    }

    private void setRedirectPage(byte[] redirectPage) {
        this.RedirectPage = redirectPage;
    }

    private void initFiles() throws IOException {
        // REDIRECT_RECORDED file init
        File file = new File(WEB_ROOT, REDIRECT_RECORDED);
        this.RedirectRecordedPageLength = (int) file.length();
        this.setRedirectRecordedPage(readFileData(file, this.RedirectRecordedPageLength));

        // REDIRECT file init
        file = new File(WEB_ROOT, REDIRECT);
        this.RedirectPageLength = (int) file.length();
        this.setRedirectPage(readFileData(file, this.RedirectPageLength));

        // NOTFOUND file init
        file = new File(WEB_ROOT, FILE_NOT_FOUND);
        this.NotFoundPageLength = (int) file.length();
        this.setNotFoundPage(readFileData(file, this.NotFoundPageLength));
    }

    private static byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return fileData;
    }
}
