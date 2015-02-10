package de.lmu.navigator.server.data;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

import java.io.IOException;
import java.util.List;

import static de.lmu.navigator.server.data.RoomCoordsImporter.logVerbose;

public class PdfTextLocalizer extends PDFTextStripper {

    private PDPage page;
    private float pageHeight;
    private float pageWidth;
    private List<RoomCoordsImporter.RoomImportResult> rooms;

    private int currentCharIndex = 0;
    private int currentRoomIndex = 0;
    private int nextStop = -1;

    private RoomCoordsImporter.RoomImportResult currentRoom;

    private String pdfText;

    public PdfTextLocalizer(PDDocument document, List<RoomCoordsImporter.RoomImportResult> rooms, String pdfText)
            throws IOException {
        super();

        this.page = (PDPage) document.getDocumentCatalog().getAllPages().get(0);

        // Get Dimensions of PDF
        int pdfRotation = this.page.findRotation();
    	if (pdfRotation == 270 || pdfRotation == 90 ||
    			pdfRotation == -270 || pdfRotation == -90) {
    		// PDF rotated in meta data >> switch height / width
            this.pageHeight = page.getMediaBox().getWidth();
            this.pageWidth = page.getMediaBox().getHeight();
            
            System.out.println("> PDF-ROTATION: " + page.findRotation()+ " °");
        }
    	else {
    		// normal 
            this.pageHeight = page.getMediaBox().getHeight();
            this.pageWidth = page.getMediaBox().getWidth();
    	}
                
        this.rooms = rooms;
        this.pdfText = pdfText;

        setWordSeparator(" ");
    }

    public void findTextPositions() throws IOException {
        findNext();
        processStream(page, page.findResources(), page.getContents().getStream());
    }

    private void findNext() {
        if (currentRoomIndex > rooms.size() - 1) {
            // no rooms left
            nextStop = -1;
            return;
        }

        currentRoom = rooms.get(currentRoomIndex);
        if (currentRoom.pdfPosition < 0) {
            // room was not found, cannot be localized
            currentRoomIndex++;
            findNext();
            return;
        }

        nextStop = currentRoom.pdfPosition;
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        if (nextStop == -1) {
            return;
        }

        if (!text.getCharacter().equals(String.valueOf(pdfText.charAt(currentCharIndex)))) {
            logVerbose("     [ bumb ]");
            currentCharIndex++;
        }

        if (currentCharIndex >= nextStop) {
            logVerbose("found " + currentRoom.room);
            currentRoom.xPos = text.getXDirAdj() / pageWidth;
            currentRoom.yPos = text.getYDirAdj() / pageHeight;

            currentRoomIndex++;
            findNext();
        }

        logVerbose(text.getCharacter() + "/" + pdfText.charAt(currentCharIndex) + "  (" + currentCharIndex + "<-" + nextStop + ")");
        currentCharIndex++;
    }
}
