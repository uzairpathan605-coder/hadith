package com.example.data.db

import com.example.data.model.BookEntity
import com.example.data.model.CollectionEntity
import com.example.data.model.HadithEntity
import com.example.data.model.SourceType

object SeedData {

    val collections = listOf(
        CollectionEntity(
            name = "bukhari",
            titleEnglish = "Sahih al-Bukhari",
            titleArabic = "صحيح البخاري",
            titleUrdu = "صحیح بخاری",
            author = "Imam Muhammad al-Bukhari (194–256 AH)",
            shortIntro = "The most authentic compilation of Hadith traditions in Sunni Islam, compiled with rigorous criteria.",
            totalHadith = 7563,
            hasBooks = true,
            availableLanguages = "ar,en,ur"
        ),
        CollectionEntity(
            name = "muslim",
            titleEnglish = "Sahih Muslim",
            titleArabic = "صحيح مسلم",
            titleUrdu = "صحیح مسلم",
            author = "Imam Muslim ibn al-Hajjaj (204–261 AH)",
            shortIntro = "The second of the Sahihayn, renowned for its pristine transmission chains and thematic arrangement.",
            totalHadith = 7500,
            hasBooks = true,
            availableLanguages = "ar,en,ur"
        )
    )

    val books = listOf(
        // Bukhari Books
        BookEntity("bukhari_1", "bukhari", "1", "Revelation", "بدء الوحي", "وحی کی شروعات", 7),
        BookEntity("bukhari_2", "bukhari", "2", "Belief (Faith)", "كتاب الإيمان", "کتاب ایمان", 58),
        BookEntity("bukhari_3", "bukhari", "3", "Knowledge", "كتاب العلم", "کتاب علم", 77),
        BookEntity("bukhari_8", "bukhari", "8", "Prayers (Salat)", "كتاب الصلاة", "کتاب نماز", 149),
        // Muslim Books
        BookEntity("muslim_1", "muslim", "1", "The Book of Faith", "كتاب الإيمان", "ایمان کا بیان", 442),
        BookEntity("muslim_2", "muslim", "2", "The Book of Purification", "كتاب الطهارة", "طہارت کا بیان", 141),
        BookEntity("muslim_32", "muslim", "32", "Virtues and Good Manners", "كتاب البر والصلة والآداب", "حسن سلوک اور صلہ رحمی", 217),
        BookEntity("muslim_33", "muslim", "33", "The Book on Leadership (Kitab al-Imara)", "كتاب الإمارة", "امارت اور جہاد کا بیان", 232)
    )

    val hadiths = listOf(
        // Sahih Bukhari 1
        HadithEntity(
            id = "bukhari_1",
            collectionName = "bukhari",
            bookNumber = "1",
            hadithNumber = "1",
            inBookReference = "Book 1, Hadith 1",
            arabicText = "حَدَّثَنَا الْحُمَيْدِيُّ عَبْدُ اللَّهِ بْنُ الزُّبَيْرِ، قَالَ حَدَّثَنَا سُفْيَانُ، قَالَ حَدَّثَنَا يَحْيَى بْنُ سَعِيدٍ الأَنْصَارِيُّ، قَالَ أَخْبَرَنِي مُحَمَّدُ بْنُ إِبْرَاهِيمَ التَّيْمِيُّ، أَنَّهُ سَمِعَ عَلْقَمَةَ بْنَ وَقَّاصٍ اللَّيْثِيَّ، يَقُولُ سَمِعْتُ عُمَرَ بْنَ الْخَطَّابِ ـ رضى الله عنه ـ عَلَى الْمِنْبَرِ قَالَ سَمِعْتُ رَسُولَ اللَّهِ صلى الله عليه وسلم يَقُولُ:\n\n«إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى، فَمَنْ كَانَتْ هِجْرَتُهُ إِلَى دُنْيَا يُصِيبُهَا أَوْ إِلَى امْرَأَةٍ يَنْكِحُهَا، فَهِجْرَتُهُ إِلَى مَا هَاجَرَ إِلَيْهِ».",
            englishText = "Narrated 'Umar bin Al-Khattab (may Allah be pleased with him): I heard Allah's Messenger (peace and blessings be upon him) saying, 'The reward of deeds depends upon the intentions and every person will get the reward according to what he has intended. So whoever emigrated for worldly benefits or for a woman to marry, his emigration was for what he emigrated for.'",
            urduText = "حضرت عمر بن خطاب رضی اللہ عنہ سے روایت ہے کہ انہوں نے منبر پر فرمایا: میں نے رسول اللہ صلی اللہ علیہ وسلم کو یہ فرماتے ہوئے سنا: 'اعمال کا دارومدار نیتوں پر ہے اور ہر انسان کے لیے وہی ہے جس کی اس نے نیت کی۔ پس جس کی ہجرت دنیا حاصل کرنے کے لیے ہو یا کسی عورت سے نکاح کے لیے، تو اس کی ہجرت اسی کے لیے ہے جس کی طرف اس نے ہجرت کی۔'",
            narrator = "Umar ibn al-Khattab (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Imam al-Bukhari",
            chapterTitleArabic = "باب كَيْفَ كَانَ بَدْءُ الْوَحْىِ إِلَى رَسُولِ اللَّهِ صلى الله عليه وسلم",
            chapterTitleEnglish = "How the Divine Revelation started being revealed to Allah's Messenger",
            chapterTitleUrdu = "رسول اللہ صلی اللہ علیہ وسلم پر وحی کی ابتدا کیسے ہوئی",
            sourceUrl = "https://sunnah.com/bukhari:1",
            isBookmarked = true,
            sourceType = SourceType.CANONICAL_VERIFIED
        ),
        // Sahih Bukhari 2
        HadithEntity(
            id = "bukhari_2",
            collectionName = "bukhari",
            bookNumber = "1",
            hadithNumber = "2",
            inBookReference = "Book 1, Hadith 2",
            arabicText = "حَدَّثَنَا عَبْدُ اللَّهِ بْنُ يُوسُفَ، قَالَ أَخْبَرَنَا مَالِكٌ، عَنْ هِشَامِ بْنِ عُرْوَةَ، عَنْ أَبِيهِ، عَنْ عَائِشَةَ أُمِّ الْمُؤْمِنِينَ ـ رضى الله عنها ـ أَنَّ الْحَارِثَ بْنَ هِشَامٍ ـ رضى الله عنه ـ سَأَلَ رَسُولَ اللَّهِ صلى الله عليه وسلم فَقَالَ يَا رَسُولَ اللَّهِ كَيْفَ يَأْتِيكَ الْوَحْىُ فَقَالَ رَسُولُ اللَّهِ صلى الله عليه وسلم:\n\n«أَحْيَانًا يَأْتِينِي مِثْلَ صَلْصَلَةِ الْجَرَسِ ـ وَهُوَ أَشَدُّهُ عَلَىَّ ـ فَيُفْصَمُ عَنِّي وَقَدْ وَعَيْتُ عَنْهُ مَا قَالَ، وَأَحْيَانًا يَتَمَثَّلُ لِي الْمَلَكُ رَجُلاً فَيُكَلِّمُنِي فَأَعِي مَا يَقُولُ». قَالَتْ عَائِشَةُ: وَلَقَدْ رَأَيْتُهُ يَنْزِلُ عَلَيْهِ الْوَحْىُ فِي الْيَوْمِ الشَّدِيدِ الْبَرْدِ، فَيَفْصِمُ عَنْهُ وَإِنَّ جَبِينَهُ لَيَتَفَصَّدُ عَرَقًا.",
            englishText = "Narrated 'Aisha, the Mother of the Believers: Al-Harith bin Hisham asked Allah's Messenger (peace be upon him): 'O Allah's Messenger! How is the Divine Inspiration revealed to you?' Allah's Messenger replied: 'Sometimes it comes to me like the ringing of a bell, and that is the hardest on me; then it leaves me while I have grasped what was said. At other times, the Angel comes to me in the likeness of a man and speaks to me, and I retain what he says.' 'Aisha added: 'Verily I saw the Prophet being inspired on a very cold day, and when the Inspiration was over, his forehead was dripping with sweat.'",
            urduText = "ام المؤمنین حضرت عائشہ رضی اللہ عنہا سے روایت ہے کہ حارث بن ہشام رضی اللہ عنہ نے رسول اللہ صلی اللہ علیہ وسلم سے پوچھا: یا رسول اللہ! آپ کے پاس وحی کس طرح آتی ہے؟ رسول اللہ نے فرمایا: 'کبھی تو میرے پاس گھنٹی کی جھنکار کی طرح آتی ہے اور یہ مجھ پر سب سے زیادہ سخت ہوتی ہے، پھر جب وہ کیفیت مجھ سے دور ہوتی ہے تو جو کچھ فرشتے نے کہا ہوتا ہے مجھے یاد ہو چکا ہوتا ہے۔ اور کبھی فرشتہ میرے سامنے ایک مرد کی صورت میں آتا ہے اور مجھ سے کلام کرتا ہے اور میں اس کی کہی ہوئی بات کو یاد کر لیتا ہوں۔' حضرت عائشہ فرماتی ہیں کہ سخت سردی کے دن میں نے دیکھا کہ آپ پر وحی نازل ہوئی اور جب وہ کیفیت ختم ہوئی تو آپ کی پیشانی مبارک سے پسینہ بہہ رہا تھا۔",
            narrator = "Aisha, Mother of the Believers (رضي الله عنها)",
            grade = "Sahih",
            gradedBy = "Imam al-Bukhari",
            chapterTitleArabic = "باب كَيْفَ كَانَ بَدْءُ الْوَحْىِ",
            chapterTitleEnglish = "How Revelation was revealed",
            chapterTitleUrdu = "وحی کی کیفیات",
            sourceUrl = "https://sunnah.com/bukhari:2",
            isBookmarked = false,
            sourceType = SourceType.CANONICAL_VERIFIED
        ),
        // Sahih Bukhari 8
        HadithEntity(
            id = "bukhari_8",
            collectionName = "bukhari",
            bookNumber = "2",
            hadithNumber = "8",
            inBookReference = "Book 2, Hadith 1",
            arabicText = "حَدَّثَنَا عَبْدُ اللَّهِ بْنُ مُوسَى، قَالَ أَخْبَرَنَا حَنْظَلَةُ بْنُ أَبِي سُفْيَانَ، عَنْ عِكْرِمَةَ بْنِ خَالِدٍ، عَنِ ابْنِ عُمَرَ، رَضِيَ اللَّهُ عَنْهُمَا قَالَ: قَالَ رَسُولُ اللَّهِ صلى الله عليه وسلم:\n\n«بُنِيَ الإِسْلاَمُ عَلَى خَمْسٍ: شَهَادَةِ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَإِقَامِ الصَّلاَةِ، وَإِيتَاءِ الزَّكَاةِ، وَالْحَجِّ، وَصَوْمِ رَمَضَانَ».",
            englishText = "Narrated Ibn 'Umar (may Allah be pleased with them both): Allah's Messenger (peace be upon him) said: 'Islam is based on five (principles): To testify that none has the right to be worshipped but Allah and Muhammad is Allah's Messenger, to establish prayers, to pay Zakat, to perform Hajj, and to observe fast during Ramadan.'",
            urduText = "حضرت عبداللہ بن عمر رضی اللہ عنہما سے روایت ہے کہ رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: 'اسلام کی بنیاد پانچ چیزوں پر رکھی گئی ہے: اس بات کی گواہی دینا کہ اللہ کے سوا کوئی معبود نہیں اور محمد اللہ کے رسول ہیں، نماز قائم کرنا، زکوٰۃ ادا کرنا، حج کرنا، اور رمضان کے روزے رکھنا۔'",
            narrator = "Abdullah ibn Umar (رضي الله عنهما)",
            grade = "Sahih",
            gradedBy = "Imam al-Bukhari",
            chapterTitleArabic = "باب بُنِيَ الإِسْلاَمُ عَلَى خَمْسٍ",
            chapterTitleEnglish = "Islam is based on five principles",
            chapterTitleUrdu = "اسلام کی بنیاد پانچ باتوں پر ہے",
            sourceUrl = "https://sunnah.com/bukhari:8",
            isBookmarked = false,
            sourceType = SourceType.CANONICAL_VERIFIED
        ),
        // Sahih Bukhari 9
        HadithEntity(
            id = "bukhari_9",
            collectionName = "bukhari",
            bookNumber = "2",
            hadithNumber = "9",
            inBookReference = "Book 2, Hadith 2",
            arabicText = "حَدَّثَنَا عَبْدُ اللَّهِ بْنُ مُحَمَّدٍ، قَالَ حَدَّثَنَا أَبُو عَامِرٍ الْعَقَدِيُّ، قَالَ حَدَّثَنَا سُلَيْمَانُ بْنُ بِلاَلٍ، عَنْ عَبْدِ اللَّهِ بْنِ دِينَارٍ، عَنْ أَبِي صَالِحٍ، عَنْ أَبِي هُرَيْرَةَ ـ رضى الله عنه ـ عَنِ النَّبِيِّ صلى الله عليه وسلم قَالَ:\n\n«الإِيمَانُ بِضْعٌ وَسِتُّونَ شُعْبَةً، وَالْحَيَاءُ شُعْبَةٌ مِنَ الإِيمَانِ».",
            englishText = "Narrated Abu Huraira: The Prophet (peace be upon him) said: 'Faith (Belief) has more than sixty branches (i.e. parts), and Haya (modesty, self-respect) is a branch of faith.'",
            urduText = "حضرت ابوہریرہ رضی اللہ عنہ سے روایت ہے کہ نبی کریم صلی اللہ علیہ وسلم نے فرمایا: 'ایمان کی ساٹھ سے کچھ اوپر شاخیں ہیں، اور حیا بھی ایمان کی ایک اہم شاخ ہے۔'",
            narrator = "Abu Hurairah (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Imam al-Bukhari",
            chapterTitleArabic = "باب أُمُورُ الإِيمَانِ",
            chapterTitleEnglish = "Matters of Faith",
            chapterTitleUrdu = "ایمان کے امور اور شاخیں",
            sourceUrl = "https://sunnah.com/bukhari:9",
            isBookmarked = false,
            sourceType = SourceType.CANONICAL_VERIFIED
        ),
        // Sahih Bukhari 10
        HadithEntity(
            id = "bukhari_10",
            collectionName = "bukhari",
            bookNumber = "2",
            hadithNumber = "10",
            inBookReference = "Book 2, Hadith 3",
            arabicText = "حَدَّثَنَا سَعِيدُ بْنُ يَحْيَى بْنِ سَعِيدٍ الْقُرَشِيُّ، قَالَ حَدَّثَنَا أَبِي قَالَ، حَدَّثَنَا أَبُو بُرْدَةَ بْنُ عَبْدِ اللَّهِ بْنِ أَبِي بُرْدَةَ، عَنْ أَبِي بُرْدَةَ، عَنْ أَبِي مُوسَى، قَالَ قَالُوا يَا رَسُولَ اللَّهِ أَىُّ الإِسْلاَمِ أَفْضَلُ قَالَ:\n\n«مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ».",
            englishText = "Narrated Abu Musa: Some people asked Allah's Messenger (peace be upon him): 'Whose Islam is the best?' He replied: 'One from whose tongue and hands Muslims are safe.'",
            urduText = "حضرت ابو موسیٰ اشعری رضی اللہ عنہ سے روایت ہے کہ صحابہ نے عرض کیا: یا رسول اللہ! کون سا مسلمان افضل ہے؟ آپ نے فرمایا: 'وہ جس کی زبان اور ہاتھ سے دوسرے مسلمان محفوظ رہیں۔'",
            narrator = "Abu Musa al-Ash'ari (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Imam al-Bukhari",
            chapterTitleArabic = "باب الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ",
            chapterTitleEnglish = "A Muslim is the one from whose tongue and hands Muslims are safe",
            chapterTitleUrdu = "کامل مسلمان وہ ہے جس کے ہاتھ اور زبان سے مسلمان محفوظ رہیں",
            sourceUrl = "https://sunnah.com/bukhari:10",
            isBookmarked = false,
            sourceType = SourceType.CANONICAL_VERIFIED
        ),
        // Sahih Bukhari 13
        HadithEntity(
            id = "bukhari_13",
            collectionName = "bukhari",
            bookNumber = "2",
            hadithNumber = "13",
            inBookReference = "Book 2, Hadith 6",
            arabicText = "حَدَّثَنَا مُسَدَّدٌ، قَالَ حَدَّثَنَا يَحْيَى، عَنْ شُعْبَةَ، عَنْ قَتَادَةَ، عَنْ أَنَسٍ، عَنِ النَّبِيِّ صلى الله عليه وسلم قَالَ:\n\n«لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ».",
            englishText = "Narrated Anas: The Prophet (peace be upon him) said: 'None of you will have faith until he loves for his brother (or neighbor) what he loves for himself.'",
            urduText = "حضرت انس رضی اللہ عنہ سے روایت ہے کہ نبی کریم صلی اللہ علیہ وسلم نے فرمایا: 'تم میں سے کوئی شخص اس وقت تک سچا مومن نہیں ہو سکتا جب تک کہ وہ اپنے بھائی کے لیے وہی پسند نہ کرے جو اپنے لیے پسند کرتا ہے۔'",
            narrator = "Anas ibn Malik (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Imam al-Bukhari",
            chapterTitleArabic = "باب مِنَ الإِيمَانِ أَنْ يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            chapterTitleEnglish = "To love for one's brother what one loves for himself is part of Faith",
            chapterTitleUrdu = "اپنے بھائی کے لیے وہی چاہنا جو اپنے لیے چاہے، ایمان کا حصہ ہے",
            sourceUrl = "https://sunnah.com/bukhari:13",
            isBookmarked = true,
            sourceType = SourceType.CANONICAL_VERIFIED
        ),
        // Sahih Bukhari 52
        HadithEntity(
            id = "bukhari_52",
            collectionName = "bukhari",
            bookNumber = "2",
            hadithNumber = "52",
            inBookReference = "Book 2, Hadith 45",
            arabicText = "عَنِ النُّعْمَانِ بْنِ بَشِيرٍ، قَالَ سَمِعْتُ رَسُولَ اللَّهِ صلى الله عليه وسلم يَقُولُ:\n\n«الْحَلاَلُ بَيِّنٌ وَالْحَرَامُ بَيِّنٌ، وَبَيْنَهُمَا مُشَبَّهَاتٌ لاَ يَعْلَمُهَا كَثِيرٌ مِنَ النَّاسِ، فَمَنِ اتَّقَى الْمُشَبَّهَاتِ اسْتَبْرَأَ لِدِينِهِ وَعِرْضِهِ، وَمَنْ وَقَعَ فِي الشُّبُهَاتِ كَرَاعٍ يَرْعَى حَوْلَ الْحِمَى، يُوشِكُ أَنْ يُوَاقِعَهُ، أَلاَ وَإِنَّ لِكُلِّ مَلِكٍ حِمًى، أَلاَ إِنَّ حِمَى اللَّهِ فِي أَرْضِهِ مَحَارِمُهُ، أَلاَ وَإِنَّ فِي الْجَسَدِ مُضْغَةً إِذَا صَلَحَتْ صَلَحَ الْجَسَدُ كُلُّهُ، وَإِذَا فَسَدَتْ فَسَدَ الْجَسَدُ كُلُّهُ، أَلاَ وَهِيَ الْقَلْبُ».",
            englishText = "Narrated An-Nu'man bin Bashir: I heard Allah's Messenger (peace be upon him) saying: 'Both legal and illegal things are obvious, and in between them are doubtful matters about which many people have no knowledge. So whoever avoids doubtful things saves his religion and honor; and whoever falls into doubtful matters falls into that which is unlawful, like a shepherd grazing near a private pasture, on the verge of grazing inside it. Truly, every king has a sanctuary, and the sanctuary of Allah on earth is what He has forbidden. Truly, in the body there is a piece of flesh: if it is upright, the whole body is upright; and if it is corrupt, the whole body is corrupt. Truly, it is the heart.'",
            urduText = "حضرت نعمان بن بشیر رضی اللہ عنہ سے روایت ہے کہ میں نے رسول اللہ صلی اللہ علیہ وسلم کو فرماتے سنا: 'حلال بھی واضح ہے اور حرام بھی واضح ہے، اور ان دونوں کے درمیان کچھ مشتبہ چیزیں ہیں جنہیں بہت سے لوگ نہیں جانتے۔ پس جو شخص شبہات سے بچ گیا اس نے اپنے دین اور عزت کو محفوظ کر لیا، اور جو شبہات میں پڑ گیا وہ گویا حرام میں پڑ گیا، اس چرواہے کی طرح جو محفوظ چراگاہ کے اردگرد بکریاں چرائے تو قریب ہے کہ اس میں چرنے لگے۔ سن لو! ہر بادشاہ کی ایک چراگاہ ہوتی ہے اور اللہ کی چراگاہ اس کی حرام کردہ چیزیں ہیں۔ اور سن لو! جسم میں ایک گوشت کا لوتھڑا ہے، جب وہ درست ہو تو سارا جسم درست رہتا ہے اور جب وہ خراب ہو جائے تو سارا جسم خراب ہو جاتا ہے؛ سن لو وہ دل ہے۔'",
            narrator = "An-Nu'man ibn Bashir (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Imam al-Bukhari",
            chapterTitleArabic = "باب فَضْلِ مَنِ اسْتَبْرَأَ لِدِينِهِ",
            chapterTitleEnglish = "The virtue of one who saves his religion from doubts",
            chapterTitleUrdu = "مشتبہات سے بچنے کی فضیلت",
            sourceUrl = "https://sunnah.com/bukhari:52",
            isBookmarked = false,
            sourceType = SourceType.CANONICAL_VERIFIED
        ),
        // Sahih Muslim 1
        HadithEntity(
            id = "muslim_1",
            collectionName = "muslim",
            bookNumber = "1",
            hadithNumber = "1",
            inBookReference = "Book 1, Hadith 1",
            arabicText = "عَنْ يَحْيَى بْنِ يَعْمَرَ، قَالَ كَانَ أَوَّلَ مَنْ قَالَ فِي الْقَدَرِ بِالْبَصْرَةِ مَعْبَدٌ الْجُهَنِيُّ... عَنْ عُمَرَ بْنِ الْخَطَّابِ قَالَ: بَيْنَمَا نَحْنُ عِنْدَ رَسُولِ اللَّهِ صلى الله عليه وسلم ذَاتَ يَوْمٍ إِذْ طَلَعَ عَلَيْنَا رَجُلٌ شَدِيدُ بَيَاضِ الثِّيَابِ شَدِيدُ سَوَادِ الشَّعَرِ لاَ يُرَى عَلَيْهِ أَثَرُ السَّفَرِ وَلاَ يَعْرِفُهُ مِنَّا أَحَدٌ... فَقَالَ رَسُولُ اللَّهِ صلى الله عليه وسلم:\n\n«الإِسْلاَمُ أَنْ تَشْهَدَ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَتُقِيمَ الصَّلاَةَ، وَتُؤْتِيَ الزَّكَاةَ، وَتَصُومَ رَمَضَانَ، وَتَحُجَّ الْبَيْتَ إِنِ اسْتَطَعْتَ إِلَيْهِ سَبِيلاً... وَالإِيمَانُ أَنْ تُؤْمِنَ بِاللَّهِ وَمَلاَئِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ وَالْيَوْمِ الآخِرِ وَتُؤْمِنَ بِالْقَدَرِ خَيْرِهِ وَشَرِّهِ».",
            englishText = "It is narrated on the authority of 'Umar ibn al-Khattab: One day while we were sitting with the Messenger of Allah (peace be upon him), there appeared before us a man dressed in extremely white clothes and with extremely black hair... The Messenger of Allah said: 'Islam is to testify that there is no deity worthy of worship but Allah and that Muhammad is the Messenger of Allah, to establish prayer, to pay Zakat, to fast Ramadan, and to perform Hajj to the House if you can find a way... And Faith is to believe in Allah, His Angels, His Books, His Messengers, the Last Day, and to believe in Destiny, its good and its evil.'",
            urduText = "حضرت عمر بن خطاب رضی اللہ عنہ سے روایت ہے: ایک دن ہم رسول اللہ صلی اللہ علیہ وسلم کے پاس بیٹھے تھے کہ اچانک ایک شخص ظاہر ہوا جس کے کپڑے انتہائی سفید اور بال بے حد سیاہ تھے، اس پر سفر کا کوئی اثر نہ تھا... رسول اللہ نے فرمایا: 'اسلام یہ ہے کہ تم گواہی دو کہ اللہ کے سوا کوئی معبود نہیں اور محمد اللہ کے رسول ہیں، نماز قائم کرو، زکوٰۃ دو، رمضان کے روزے رکھو اور اگر استطاعت ہو تو بیت اللہ کا حج کرو... اور ایمان یہ ہے کہ تم اللہ پر، اس کے فرشتوں پر، اس کی کتابوں پر، اس کے رسولوں پر، آخرت کے دن پر اور تقدیر کی اچھائی اور برائی پر یقین رکھو۔'",
            narrator = "Umar ibn al-Khattab (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Imam Muslim",
            chapterTitleArabic = "باب مَعْرِفَةِ الإِيمَانِ وَالإِسْلاَمِ وَالْقَدَرِ",
            chapterTitleEnglish = "Clarification of Faith, Islam and Destiny",
            chapterTitleUrdu = "ایمان، اسلام اور تقدیر کا بیان",
            sourceUrl = "https://sunnah.com/muslim:1",
            isBookmarked = true,
            sourceType = SourceType.CANONICAL_VERIFIED
        ),
        // Sahih Muslim 16
        HadithEntity(
            id = "muslim_16",
            collectionName = "muslim",
            bookNumber = "1",
            hadithNumber = "16",
            inBookReference = "Book 1, Hadith 74",
            arabicText = "عَنْ أَبِي هُرَيْرَةَ، عَنْ رَسُولِ اللَّهِ صلى الله عليه وسلم قَالَ:\n\n«مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ، وَمَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيُكْرِمْ جَارَهُ، وَمَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيُكْرِمْ ضَيْفَهُ».",
            englishText = "Abu Huraira reported that the Messenger of Allah (peace be upon him) said: 'Whoever believes in Allah and the Last Day should speak good or remain silent; and whoever believes in Allah and the Last Day should be hospitable to his neighbor; and whoever believes in Allah and the Last Day should honor his guest.'",
            urduText = "حضرت ابوہریرہ رضی اللہ عنہ سے روایت ہے کہ رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: 'جو شخص اللہ اور آخرت کے دن پر ایمان رکھتا ہو اسے چاہیے کہ اچھی بات کہے یا خاموش رہے؛ اور جو اللہ اور آخرت پر ایمان رکھتا ہو وہ اپنے پڑوسی کا احترام کرے؛ اور جو اللہ اور آخرت پر ایمان رکھتا ہو وہ اپنے مہمان کی عزت کرے۔'",
            narrator = "Abu Hurairah (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Imam Muslim",
            chapterTitleArabic = "باب الْحَثِّ عَلَى إِكْرَامِ الْجَارِ وَالضَّيْفِ وَلُزُومِ الصَّمْتِ",
            chapterTitleEnglish = "Urging hospitality to neighbors and guests, and maintaining silence except for good",
            chapterTitleUrdu = "پڑوسی اور مہمان کی عزت اور خیر کے سوا خاموشی کی ترغیب",
            sourceUrl = "https://sunnah.com/muslim:16",
            isBookmarked = false,
            sourceType = SourceType.CANONICAL_VERIFIED
        ),
        // Sahih Muslim 23
        HadithEntity(
            id = "muslim_23",
            collectionName = "muslim",
            bookNumber = "2",
            hadithNumber = "23",
            inBookReference = "Book 2, Hadith 1",
            arabicText = "عَنْ أَبِي مَالِكٍ الأَشْعَرِيِّ، قَالَ قَالَ رَسُولُ اللَّهِ صلى الله عليه وسلم:\n\n«الطُّهُورُ شَطْرُ الإِيمَانِ، وَالْحَمْدُ لِلَّهِ تَمْلأُ الْمِيزَانَ، وَسُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ تَمْلآنِ ـ أَوْ تَمْلأُ ـ مَا بَيْنَ السَّمَاوَاتِ وَالأَرْضِ، وَالصَّلاَةُ نُورٌ، وَالصَّدَقَةُ بُرْهَانٌ، وَالصَّبْرُ ضِيَاءٌ، وَالْقُرْآنُ حُجَّةٌ لَكَ أَوْ عَلَيْكَ، كُلُّ النَّاسِ يَغْدُو فَبَائِعٌ نَفْسَهُ فَمُعْتِقُهَا أَوْ مُوبِقُهَا».",
            englishText = "Abu Malik Al-Ash'ari reported: The Messenger of Allah (peace be upon him) said: 'Cleanliness (purification) is half of faith and Alhamdulillah (Praise be to Allah) fills the scale, and SubhanAllah and Alhamdulillah fill what is between heaven and earth. Prayer is a light, charity is a proof, patience is illumination, and the Qur'an is an argument for you or against you. Every person goes out in the morning and sells himself, either freeing himself or bringing about his ruin.'",
            urduText = "حضرت ابو مالک اشعری رضی اللہ عنہ سے روایت ہے کہ رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: 'طہارت نصف ایمان ہے، اور الحمد للہ میزان کو بھر دیتا ہے، اور سبحان اللہ اور الحمد للہ آسمانوں اور زمین کے درمیان کے خلا کو بھر دیتے ہیں۔ اور نماز نور ہے، صدقہ برہان و دلیل ہے، صبر روشنی ہے اور قرآن تمہارے حق میں یا تمہارے خلاف حجت ہے۔ ہر انسان صبح کرتا ہے اور اپنی جان کا سودا کرتا ہے، یا تو اسے آزاد کر لیتا ہے یا اسے تباہ کر دیتا ہے۔'",
            narrator = "Abu Malik al-Ash'ari (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Imam Muslim",
            chapterTitleArabic = "باب فَضْلِ الْوُضُوءِ",
            chapterTitleEnglish = "The virtue of Wudu and purification",
            chapterTitleUrdu = "وضو اور طہارت کی فضیلت",
            sourceUrl = "https://sunnah.com/muslim:23",
            isBookmarked = true,
            sourceType = SourceType.CANONICAL_VERIFIED
        ),
        // Sahih Muslim 1900 (Guiding to Goodness)
        HadithEntity(
            id = "muslim_1900",
            collectionName = "muslim",
            bookNumber = "33",
            hadithNumber = "1900",
            inBookReference = "Book 33, Hadith 1893",
            arabicText = "عَنْ أَبِي مَسْعُودٍ الأَنْصَارِيِّ، قَالَ: جَاءَ رَجُلٌ إِلَى النَّبِيِّ صلى الله عليه وسلم فَقَالَ: إِنِّي أُبْدِعَ بِي فَاحْمِلْنِي. فَقَالَ: «مَا عِنْدِي». فَقَالَ رَجُلٌ: يَا رَسُولَ اللَّهِ أَنَا أَدُلُّهُ عَلَى مَنْ يَحْمِلُهُ. فَقَالَ رَسُولُ اللَّهِ صلى الله عليه وسلم:\n\n«مَنْ دَلَّ عَلَى خَيْرٍ فَلَهُ مِثْلُ أَجْرِ فَاعِلِهِ».",
            englishText = "Abu Mas'ud al-Ansari reported that a man came to the Prophet (peace be upon him) and said: 'My mount has broken down, so provide me with a mount.' The Prophet said: 'I have none.' A man said: 'O Messenger of Allah, I can guide him to someone who will give him a mount.' The Messenger of Allah (peace be upon him) said: 'Whoever guides someone to goodness will have a reward like one who did it.'",
            urduText = "حضرت ابو مسعود انصاری رضی اللہ عنہ سے روایت ہے کہ ایک شخص رسول اللہ صلی اللہ علیہ وسلم کی خدمت میں حاضر ہوا اور عرض کیا: میری سواری تھک کر رہ گئی ہے، مجھے کوئی سواری عطا فرمائیے۔ آپ نے فرمایا: 'میرے پاس کوئی سواری نہیں۔' ایک اور شخص نے عرض کیا: یا رسول اللہ! میں اس کی رہنمائی ایسے شخص کی طرف کر دیتا ہوں جو اسے سواری دے دے گا۔ اس پر رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: 'جس نے کسی نیکی کی رہنمائی کی، اس کے لیے نیکی کرنے والے کے برابر ثواب ہے۔'",
            narrator = "Abu Mas'ud al-Ansari (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Imam Muslim",
            chapterTitleArabic = "باب فَضْلِ إِعَانَةِ الْغَازِي فِي سَبِيلِ اللَّهِ وَمَنْ دَلَّ عَلَى خَيْرٍ",
            chapterTitleEnglish = "The virtue of one who guides to goodness",
            chapterTitleUrdu = "نیکی کی راہ دکھانے والے کی فضیلت",
            sourceUrl = "https://sunnah.com/muslim:1893",
            isBookmarked = false,
            sourceType = SourceType.CANONICAL_VERIFIED
        ),
        // Sahih Muslim 1907
        HadithEntity(
            id = "muslim_1907",
            collectionName = "muslim",
            bookNumber = "32",
            hadithNumber = "1907",
            inBookReference = "Book 32, Hadith 82",
            arabicText = "عَنْ أَنَسٍ، قَالَ قَالَ رَسُولُ اللَّهِ صلى الله عليه وسلم:\n\n«انْصُرْ أَخَاكَ ظَالِمًا أَوْ مَظْلُومًا». فَقَالَ رَجُلٌ يَا رَسُولَ اللَّهِ أَنْصُرُهُ إِذَا كَانَ مَظْلُومًا، أَفَرَأَيْتَ إِذَا كَانَ ظَالِمًا كَيْفَ أَنْصُرُهُ قَالَ: «تَحْجُزُهُ ـ أَوْ تَمْنَعُهُ ـ مِنَ الظُّلْمِ فَإِنَّ ذَلِكَ نَصْرُهُ».",
            englishText = "Anas reported Allah's Messenger (peace be upon him) as saying: 'Help your brother, whether he is an oppressor or he is an oppressed one.' A man said: 'O Messenger of Allah! I help him when he is oppressed, but if he is an oppressor, how shall I help him?' The Prophet replied: 'By preventing him or restraining him from committing oppression; that is your help to him.'",
            urduText = "حضرت انس رضی اللہ عنہ سے روایت ہے کہ رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: 'اپنے بھائی کی مدد کرو خواہ وہ ظالم ہو یا مظلوم۔' ایک شخص نے عرض کیا: یا رسول اللہ! اگر وہ مظلوم ہو تو میں اس کی مدد کروں گا، لیکن اگر وہ ظالم ہو تو میں اس کی مدد کیسے کروں؟ آپ نے فرمایا: 'تم اسے ظلم کرنے سے روکو، یہی اس کی مدد ہے۔'",
            narrator = "Anas ibn Malik (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Imam Muslim",
            chapterTitleArabic = "باب نَصْرِ الأَخِ ظَالِمًا أَوْ مَظْلُومًا",
            chapterTitleEnglish = "Helping one's brother whether oppressor or oppressed",
            chapterTitleUrdu = "ظالم اور مظلوم دونوں بھائیوں کی مدد کرنے کا بیان",
            sourceUrl = "https://sunnah.com/muslim:1907",
            isBookmarked = false,
            sourceType = SourceType.CANONICAL_VERIFIED
        ),
        // Sahih Muslim 2564
        HadithEntity(
            id = "muslim_2564",
            collectionName = "muslim",
            bookNumber = "32",
            hadithNumber = "2564",
            inBookReference = "Book 32, Hadith 32",
            arabicText = "عَنْ أَبِي هُرَيْرَةَ، أَنَّ رَسُولَ اللَّهِ صلى الله عليه وسلم قَالَ:\n\n«إِيَّاكُمْ وَالظَّنَّ فَإِنَّ الظَّنَّ أَكْذَبُ الْحَدِيثِ، وَلاَ تَحَسَّسُوا وَلاَ تَجَسَّسُوا، وَلاَ تَنَافَسُوا وَلاَ تَحَاسَدُوا، وَلاَ تَبَاغَضُوا وَلاَ تَدَابَرُوا، وَكُونُوا عِبَادَ اللَّهِ إِخْوَانًا».",
            englishText = "Abu Huraira reported Allah's Messenger (peace be upon him) as saying: 'Beware of suspicion, for suspicion is the worst of false tales; and do not look for others' faults and do not spy, and do not be jealous of one another, and do not hate one another, and do not turn away from one another, and be, servants of Allah, brothers.'",
            urduText = "حضرت ابوہریرہ رضی اللہ عنہ سے روایت ہے کہ رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: 'بدگمانی سے بچو، کیونکہ بدگمانی سب سے جھوٹی بات ہے۔ اور نہ لوگوں کے عیوب ٹٹولو، نہ جاسوسی کرو، نہ آپس میں حسد کرو، نہ بغض رکھو، اور نہ ایک دوسرے سے پیٹھ پھیرو؛ اور اللہ کے بندے بھائی بھائی بن کر رہو۔'",
            narrator = "Abu Hurairah (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Imam Muslim",
            chapterTitleArabic = "باب تَحْرِيمِ الظَّنِّ وَالتَّجَسُّسِ وَالتَّنَافُسِ",
            chapterTitleEnglish = "Prohibition of Suspicion, Spying, and Malice",
            chapterTitleUrdu = "بدگمانی، جاسوسی اور بغض کی ممانعت",
            sourceUrl = "https://sunnah.com/muslim:2564",
            isBookmarked = false,
            sourceType = SourceType.CANONICAL_VERIFIED
        )
    )
}
