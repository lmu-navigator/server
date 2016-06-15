package de.lmu.navigator.server.data;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoomCoordsImporter {
    private final static boolean VERBOSE = false;

    public static class RoomImportResult {
        public String room;
        public int pdfPosition = -1;
        public double xPos = -1;
        public double yPos = -1;

        @Override
        public String toString() {
            return room + ": pos=" + pdfPosition + " xPos=" + xPos + " yPos=" + yPos;
        }
    }

    public static void logVerbose(String log) {
        if (VERBOSE) {
            System.out.println(log);
        }
    }

	/**
	 * Method for finding Rooms in PDF file
	 * @author Timo Loewe
	 * 
	 * @param fileUrl
	 * @param roomList
	 * @return
	 * @throws IOException
	 */
    public static Map<String, double[]> findRoomCoords(URL fileUrl, List<String> roomList) throws IOException {
    	PDDocument document = PDDocument.load(fileUrl);

        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setWordSeparator(" ");
        String pdfText = stripper.getText(document);

        // for testing: print the whole document
//        logVerbose(pdfText);

        // trim elements
        for (int i = 0; i < roomList.size(); i++) {
            roomList.set(i, roomList.get(i).trim());
        }

        Collections.sort(roomList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                if (s1.length() > s2.length()) {
                    return -1;
                }

                if (s1.length() < s2.length()) {
                    return 1;
                }

                return 0;
            }
        });

        List<RoomImportResult> results = new ArrayList<RoomImportResult>(roomList.size());
        List<Integer> foundPositions = new ArrayList<Integer>();

        for (String room : roomList) {
            RoomImportResult result = new RoomImportResult();
            result.room = room;

            Matcher matcher = Pattern.compile(room).matcher(pdfText);
            int matches = 0;
            while(matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();

                // prevent conflict for multiple positions
                if (!foundPositions.contains(start)) {
                    for (int i = start; i < end; i++) {
                        foundPositions.add(i);
                    }

                    matches++;

                    if (matches == 1) {
                        result.pdfPosition = start;
                    } else {
                        result.pdfPosition = -1;
                    }
                }
            }

            if (matches > 1) {
                logVerbose(matches + " matches for room: " + room);
            } else if (matches == 0) {
                logVerbose("No matches for room: " + room);
            }

            results.add(result);
        }

        Collections.sort(results, new Comparator<RoomImportResult>() {
            @Override
            public int compare(RoomImportResult r1, RoomImportResult r2) {
                return r1.pdfPosition - r2.pdfPosition;
            }
        });

        PdfTextLocalizer localizer = new PdfTextLocalizer(document, results, pdfText);
        localizer.findTextPositions();
        document.close();
        
        // Debug Code
//        for (RoomImportResult r : results) System.out.println(r);

        Map<String, double[]> positions = new HashMap<String, double[]>(results.size());
        for (RoomImportResult r : results) {
            positions.put(r.room, new double[] {r.xPos, r.yPos});
        }

        return positions;
    }
    
}
