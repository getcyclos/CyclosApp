/*
 * Copyright (c) 2020 Gabriel Estrada <dev@getcyclos.com>
 *
 * This file is part of CyclosApp
 *
 * CyclosApp is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CyclosApp is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ve.cyclos.fitness.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import ve.cyclos.fitness.BuildConfig;
import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.ui.CyclosAppActivity;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends CyclosAppActivity {

    private AboutPage aboutPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Instance.getInstance(this).themes.getDefaultTheme());

        createAboutPage();
        setContentView(aboutPage.create());

        setupActionBar();
    }

    private void createAboutPage() {
        aboutPage = new AboutPage(this);
        aboutPage.enableDarkMode(!Instance.getInstance(this).themes.shouldUseLightMode());
        aboutPage.setImage(R.mipmap.ic_launcher_round);
        aboutPage.setDescription(getString(R.string.aboutVersion) + " " + BuildConfig.VERSION_NAME);

        aboutPage.addGroup(getString(R.string.aboutLinks));
        /*
        aboutPage.addItem(applyUrlToElement(createThemedElement(getString(R.string.aboutSourceCode)).setIconDrawable(R.drawable.ic_code), getString(R.string.urlRepository)));
        */
        aboutPage.addItem(applyUrlToElement(createThemedElement(getString(R.string.aboutPrivacyPolicy)).setIconDrawable(R.drawable.ic_privacy), getString(R.string.urlPrivacy)));
        /*
        aboutPage.addItem(applyUrlToElement(createThemedElement(getString(R.string.aboutChangelog)).setIconDrawable(R.drawable.ic_changes), getString(R.string.urlChangelog)));
        aboutPage.addItem(applyUrlToElement(createThemedElement(getString(R.string.aboutOpenSourceLibraries)).setIconDrawable(R.drawable.ic_library_books), getString(R.string.urlCopyrightNotices)));
        aboutPage.addItem(applyUrlToElement(createThemedElement(getString(R.string.aboutReportBug)).setIconDrawable(R.drawable.ic_bug_report), getString(R.string.urlBugReport)));
        aboutPage.addItem(applyUrlToElement(createThemedElement(getString(R.string.aboutHelpTranslating)).setIconDrawable(R.drawable.ic_language), getString(R.string.urlWeblate)));
        aboutPage.addItem(applyUrlToElement(createThemedElement(getString(R.string.aboutSendFeedback)).setIconDrawable(R.drawable.ic_email), "mailto:" + getString(R.string.emailAuthor)));
        */
        aboutPage.addGroup(getString(R.string.aboutAuthor));
        aboutPage.addItem(applyUrlToElement(createPersonElement(getString(R.string.authorName)), getString(R.string.urlAuthor)));
/*
        aboutPage.addGroup(getString(R.string.aboutContributors));
        addPersonsFromArray(getResources().getStringArray(R.array.contributors));
        aboutPage.addItem(createThemedElement(getString(R.string.aboutUnlistedContributors)).setIconDrawable(R.drawable.ic_group));
*/
    }

    private void addPersonsFromArray(String[] array) {
        for (String s : array) {
            aboutPage.addItem(createPersonElement(s));
        }
    }

    private Element createPersonElement(String name) {
        return createThemedElement(name).setIconDrawable(R.drawable.ic_person);
    }

    private Element createThemedElement(String title) {
        return new Element().setTitle(title).setAutoApplyIconTint(true).setIconNightTint(R.color.colorPrimaryDark);
    }

    private Element applyUrlToElement(Element element, String url) {
        return element.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });
    }


}