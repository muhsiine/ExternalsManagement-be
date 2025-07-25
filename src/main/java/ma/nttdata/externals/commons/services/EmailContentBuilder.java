package ma.nttdata.externals.commons.services;


public class EmailContentBuilder {

    public static String buildInterviewEmail(String fullName, String offerTitle, String link, String scheduledDate) {
        return """
                <div style="max-width:600px;margin:0 auto;padding:20px;font-family:sans-serif;border:1px solid #ccc;border-radius:8px;">
                    <h2 style="color:#004c97;">NTT DATA Interview</h2>
                    <hr style="border:0;height:1px;background:#ccc;margin:10px 0;">
                    <p>Hello <strong>%s</strong>,</p>
                    <p>You are invited for an interview for the position of <strong>%s</strong>.</p>
                    <p><strong>Date:</strong> %s</p>
                    <p><strong>Link:</strong> <a href="%s" target="_blank">%s</a></p>
                    <br>
                    <p style="color:#666;font-size:12px;">
                        NTT DATA<br>
                        Parc Tétouan Shore, Shore 3, Route de Cabo Negro, Martil-Maroc.
                    </p>
                </div>
                """.formatted(fullName, offerTitle, scheduledDate, link,link);
    }
}
