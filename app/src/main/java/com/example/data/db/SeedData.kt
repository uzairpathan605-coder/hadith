package com.example.data.db

import com.example.data.model.BookEntity
import com.example.data.model.CollectionEntity
import com.example.data.model.HadithEntity

object SeedData {

    val collections = listOf(
        CollectionEntity(
            name = "bukhari",
            titleEnglish = "Sahih al-Bukhari",
            titleArabic = "صحيح البخاري",
            titleUrdu = "صحیح بخاری",
            author = "Imam Muhammad al-Bukhari (194–256 AH)",
            shortIntro = "Considered by the vast majority of Muslims to be the most authentic compilation of Hadith traditions.",
            totalHadith = 7563,
            hasBooks = true
        ),
        CollectionEntity(
            name = "muslim",
            titleEnglish = "Sahih Muslim",
            titleArabic = "صحيح مسلم",
            titleUrdu = "صحیح مسلم",
            author = "Imam Muslim ibn al-Hajjaj (204–261 AH)",
            shortIntro = "The second of the two Sahihayn, celebrated for its rigorous chain analysis and thematic arrangement.",
            totalHadith = 7500,
            hasBooks = true
        ),
        CollectionEntity(
            name = "abudawud",
            titleEnglish = "Sunan Abi Dawud",
            titleArabic = "سنن أبي داود",
            titleUrdu = "سنن ابی داؤد",
            author = "Imam Abu Dawud Sulayman ibn al-Ash'ath (202–275 AH)",
            shortIntro = "Focuses primarily on legal rulings (Ahkam) and jurisprudence narrations.",
            totalHadith = 5274,
            hasBooks = true
        ),
        CollectionEntity(
            name = "tirmidhi",
            titleEnglish = "Jami' at-Tirmidhi",
            titleArabic = "جامع الترمذي",
            titleUrdu = "جامع ترمذی",
            author = "Imam Abu 'Isa Muhammad at-Tirmidhi (209–279 AH)",
            shortIntro = "Distinguished by the author's thorough critical notes on hadith gradings and juristic opinions.",
            totalHadith = 3956,
            hasBooks = true
        ),
        CollectionEntity(
            name = "nasai",
            titleEnglish = "Sunan an-Nasa'i",
            titleArabic = "سنن النسائي",
            titleUrdu = "سنن نسائی",
            author = "Imam Ahmad ibn Shu'ayb an-Nasa'i (214–303 AH)",
            shortIntro = "Known for its strict scrutiny of narrators, ranking right behind the two Sahihs.",
            totalHadith = 5758,
            hasBooks = true
        ),
        CollectionEntity(
            name = "ibnmajah",
            titleEnglish = "Sunan Ibn Majah",
            titleArabic = "سنن ابن ماجه",
            titleUrdu = "سنن ابن ماجہ",
            author = "Imam Ibn Majah Muhammad ibn Yazid (209–273 AH)",
            shortIntro = "The sixth book of the canonical Kutub al-Sittah, containing valuable unique additions (Zawa'id).",
            totalHadith = 4341,
            hasBooks = true
        ),
        CollectionEntity(
            name = "malik",
            titleEnglish = "Muwatta Malik",
            titleArabic = "موطأ مالك",
            titleUrdu = "موطأ امام مالک",
            author = "Imam Malik ibn Anas (93–179 AH)",
            shortIntro = "The earliest surviving text of Islamic law and Prophetic tradition from Medina.",
            totalHadith = 1858,
            hasBooks = true
        ),
        CollectionEntity(
            name = "ahmad",
            titleEnglish = "Musnad Ahmad",
            titleArabic = "مسند أحمد",
            titleUrdu = "مسند احمد",
            author = "Imam Ahmad ibn Hanbal (164–241 AH)",
            shortIntro = "One of the largest compilations of hadith, organized by the companion narrators.",
            totalHadith = 27647,
            hasBooks = true
        )
    )

    val books = listOf(
        // Bukhari Books
        BookEntity("bukhari_1", "bukhari", "1", "Revelation", "بدء الوحي", "وحی کی شروعات", 7),
        BookEntity("bukhari_2", "bukhari", "2", "Belief", "الإيمان", "ایمان", 58),
        BookEntity("bukhari_8", "bukhari", "8", "Prayers (Salat)", "الصلاة", "نماز", 149),
        // Muslim Books
        BookEntity("muslim_1", "muslim", "1", "The Book of Faith", "كتاب الإيمان", "ایمان کا بیان", 442),
        BookEntity("muslim_32", "muslim", "32", "Virtues and Good Manners", "كتاب البر والصلة والآداب", "حسن سلوک اور صلہ رحمی", 217),
        // Abu Dawud Books
        BookEntity("abudawud_43", "abudawud", "43", "General Behavior (Adab)", "كتاب الأدب", "اخلاق و آداب", 188),
        // Tirmidhi Books
        BookEntity("tirmidhi_27", "tirmidhi", "27", "Righteousness and Relations", "كتاب البر والصلة", "نیکی اور صلہ رحمی", 112),
        BookEntity("tirmidhi_38", "tirmidhi", "38", "Day of Resurrection & Heart-Melters", "كتاب صفة القيامة والرقائق", "قیامت کی صفت اور رقت دل", 79),
        // Nasai Books
        BookEntity("nasai_1", "nasai", "1", "Purification", "كتاب الطهارة", "طہارت کا بیان", 325),
        // Ibn Majah Books
        BookEntity("ibnmajah_1", "ibnmajah", "1", "The Book of the Sunnah", "كتاب المقدمة", "کتاب سنت اور مقدمہ", 266),
        // Muwatta Malik Books
        BookEntity("malik_47", "malik", "47", "Good Character", "كتاب حسن الخلق", "حسن اخلاق", 18),
        // Musnad Ahmad
        BookEntity("ahmad_1", "ahmad", "1", "Musnad of the Rightly-Guided Caliphs", "مسند الخلفاء الراشدين", "مسند خلفائے راشدین", 450)
    )

    val hadiths = listOf(
        // Bukhari Hadith 1
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
            isBookmarked = true
        ),
        // Bukhari Hadith 8
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
            isBookmarked = false
        ),
        // Bukhari Hadith 13
        HadithEntity(
            id = "bukhari_13",
            collectionName = "bukhari",
            bookNumber = "2",
            hadithNumber = "13",
            inBookReference = "Book 2, Hadith 6",
            arabicText = "حَدَّثَنَا مُسَدَّدٌ، قَالَ حَدَّثَنَا يَحْيَى، عَنْ شُعْبَةَ، عَنْ قَتَادَةَ، عَنْ أَنَسٍ، عَنِ النَّبِيِّ صلى الله عليه وسلم قَالَ: وَعَنْ حُسَيْنٍ الْمُعَلِّمِ، قَالَ حَدَّثَنَا قَتَادَةُ، عَنْ أَنَسٍ، عَنِ النَّبِيِّ صلى الله عليه وسلم قَالَ:\n\n«لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ».",
            englishText = "Narrated Anas (may Allah be pleased with him): The Prophet (peace and blessings be upon him) said: 'None of you will have faith until he loves for his brother (or neighbor) what he loves for himself.'",
            urduText = "حضرت انس رضی اللہ عنہ سے روایت ہے کہ نبی کریم صلی اللہ علیہ وسلم نے فرمایا: 'تم میں سے کوئی شخص اس وقت تک سچا مومن نہیں ہو سکتا جب تک کہ وہ اپنے بھائی کے لیے وہی پسند نہ کرے جو اپنے لیے پسند کرتا ہے۔'",
            narrator = "Anas ibn Malik (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Imam al-Bukhari",
            chapterTitleArabic = "باب مِنَ الإِيمَانِ أَنْ يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            chapterTitleEnglish = "To love for one's brother what he loves for himself is part of Faith",
            chapterTitleUrdu = "اپنے بھائی کے لیے وہی چاہنا جو اپنے لیے چاہے، ایمان کا حصہ ہے",
            sourceUrl = "https://sunnah.com/bukhari:13",
            isBookmarked = true
        ),
        // Muslim Hadith 8 (Hadith of Jibril)
        HadithEntity(
            id = "muslim_8",
            collectionName = "muslim",
            bookNumber = "1",
            hadithNumber = "8",
            inBookReference = "Book 1, Hadith 1",
            arabicText = "عَنْ يَحْيَى بْنِ يَعْمَرَ، قَالَ كَانَ أَوَّلَ مَنْ قَالَ فِي الْقَدَرِ بِالْبَصْرَةِ مَعْبَدٌ الْجُهَنِيُّ... عَنْ عُمَرَ بْنِ الْخَطَّابِ قَالَ: بَيْنَمَا نَحْنُ عِنْدَ رَسُولِ اللَّهِ صلى الله عليه وسلم ذَاتَ يَوْمٍ إِذْ طَلَعَ عَلَيْنَا رَجُلٌ شَدِيدُ بَيَاضِ الثِّيَابِ شَدِيدُ سَوَادِ الشَّعَرِ لاَ يُرَى عَلَيْهِ أَثَرُ السَّفَرِ وَلاَ يَعْرِفُهُ مِنَّا أَحَدٌ حَتَّى جَلَسَ إِلَى النَّبِيِّ صلى الله عليه وسلم... قَالَ: يَا مُحَمَّدُ أَخْبِرْنِي عَنِ الإِسْلاَمِ. فَقَالَ رَسُولُ اللَّهِ صلى الله عليه وسلم:\n\n«الإِسْلاَمُ أَنْ تَشْهَدَ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَتُقِيمَ الصَّلاَةَ، وَتُؤْتِيَ الزَّكَاةَ، وَتَصُومَ رَمَضَانَ، وَتَحُجَّ الْبَيْتَ إِنِ اسْتَطَعْتَ إِلَيْهِ سَبِيلاً». قَالَ صَدَقْتَ... قَالَ: فَأَخْبِرْنِي عَنِ الإِحْسَانِ. قَالَ:\n\n«أَنْ تَعْبُدَ اللَّهَ كَأَنَّكَ تَرَاهُ فَإِنْ لَمْ تَكُنْ تَرَاهُ فَإِنَّهُ يَرَاكَ».",
            englishText = "Narrated 'Umar ibn al-Khattab: One day while we were sitting with the Messenger of Allah (peace be upon him) there appeared before us a man whose clothes were exceedingly white and whose hair was exceedingly black; no signs of journeying were to be seen on him and none of us knew him... He said: 'O Muhammad, inform me about Islam.' The Messenger of Allah replied: 'Islam is to testify that there is no god but Allah and Muhammad is the Messenger of Allah, to perform prayers, to give Zakat, to fast in Ramadan, and to make the pilgrimage to the House if you are able.' He said: 'You have spoken the truth.' He then said: 'Inform me about Ihsan (excellence).' The Prophet replied: 'It is to worship Allah as though you see Him, and even if you do not see Him, He sees you.'",
            urduText = "حضرت عمر بن خطاب رضی اللہ عنہ نے فرمایا: ایک دن ہم رسول اللہ صلی اللہ علیہ وسلم کے پاس بیٹھے تھے کہ اچانک ایک آدمی ہمارے سامنے ظاہر ہوا، جس کے کپڑے انتہائی سفید اور بال نہایت سیاہ تھے، اس پر سفر کا کوئی اثر نظر نہیں آتا تھا اور ہم میں سے کوئی اسے پہچانتا بھی نہیں تھا... اس نے کہا: اے محمد! مجھے اسلام کے بارے میں بتائیے۔ رسول اللہ نے فرمایا: 'اسلام یہ ہے کہ تم گواہی دو کہ اللہ کے سوا کوئی معبود نہیں اور محمد اللہ کے رسول ہیں، نماز قائم کرو، زکوٰۃ دو، رمضان کے روزے رکھو اور اگر استطاعت ہو تو بیت اللہ کا حج کرو۔' اس نے کہا: آپ نے سچ فرمایا... پھر پوچھا: مجھے احسان کے بارے میں بتائیے۔ آپ نے فرمایا: 'احسان یہ ہے کہ تم اللہ کی عبادت اس طرح کرو گویا تم اسے دیکھ رہے ہو، اور اگر تم اسے نہ دیکھ سکو تو یقیناً وہ تمہیں دیکھ رہا ہے۔'",
            narrator = "Umar ibn al-Khattab (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Imam Muslim",
            chapterTitleArabic = "باب بَيَانِ الإِيمَانِ وَالإِسْلاَمِ وَالإِحْسَانِ",
            chapterTitleEnglish = "The Explanation of Faith, Islam, and Excellence (Ihsan)",
            chapterTitleUrdu = "ایمان، اسلام اور احسان کا بیان",
            sourceUrl = "https://sunnah.com/muslim:8",
            isBookmarked = false
        ),
        // Muslim Hadith 2564
        HadithEntity(
            id = "muslim_2564",
            collectionName = "muslim",
            bookNumber = "32",
            hadithNumber = "2564",
            inBookReference = "Book 32, Hadith 32",
            arabicText = "عَنْ أَبِي هُرَيْرَةَ، أَنَّ رَسُولَ اللَّهِ صلى الله عليه وسلم قَالَ:\n\n«إِيَّاكُمْ وَالظَّنَّ فَإِنَّ الظَّنَّ أَكْذَبُ الْحَدِيثِ، وَلاَ تَحَسَّسُوا وَلاَ تَجَسَّسُوا، وَلاَ تَنَافَسُوا وَلاَ تَحَاسَدُوا، وَلاَ تَبَاغَضُوا وَلاَ تَدَابَرُوا، وَكُونُوا عِبَادَ اللَّهِ إِخْوَانًا».",
            englishText = "Abu Huraira reported Allah's Messenger (peace be upon him) as saying: 'Beware of suspicion, for suspicion is the worst of false tales; and do not look for the others' faults and do not spy, and do not be jealous of one another, and do not hate one another, and do not turn away from one another, and be, servants of Allah, brothers.'",
            urduText = "حضرت ابوہریرہ رضی اللہ عنہ سے روایت ہے کہ رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: 'بدگمانی سے بچو، کیونکہ بدگمانی سب سے جھوٹی بات ہے۔ اور نہ لوگوں کے عیوب ٹٹولو، نہ جاسوسی کرو، نہ آپس میں حسد کرو، نہ بغض رکھو، اور نہ ایک دوسرے سے پیٹھ پھیرو؛ اور اللہ کے بندے بھائی بھائی بن کر رہو۔'",
            narrator = "Abu Hurairah (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Imam Muslim",
            chapterTitleArabic = "باب تَحْرِيمِ الظَّنِّ وَالتَّجَسُّسِ وَالتَّنَافُسِ",
            chapterTitleEnglish = "Prohibition of Suspicion, Spying, and Malice",
            chapterTitleUrdu = "بدگمانی، جاسوسی اور بغض کی ممانعت",
            sourceUrl = "https://sunnah.com/muslim:2564",
            isBookmarked = false
        ),
        // Abu Dawud Hadith 4941
        HadithEntity(
            id = "abudawud_4941",
            collectionName = "abudawud",
            bookNumber = "43",
            hadithNumber = "4941",
            inBookReference = "Book 43, Hadith 169",
            arabicText = "حَدَّثَنَا مُسَدَّدٌ، حَدَّثَنَا سُفْيَانُ، عَنْ عَمْرِو بْنِ دِينَارٍ، عَنْ أَبِي قَابُوسَ، عَنْ عَبْدِ اللَّهِ بْنِ عَمْرٍو، يَبْلُغُ بِهِ النَّبِيَّ صلى الله عليه وسلم:\n\n«الرَّاحِمُونَ يَرْحَمُهُمُ الرَّحْمَنُ، ارْحَمُوا أَهْلَ الأَرْضِ يَرْحَمْكُمْ مَنْ فِي السَّمَاءِ».",
            englishText = "Narrated Abdullah ibn Amr: The Prophet (peace be upon him) said: 'The compassionate ones are shown mercy by the Most Merciful. Show mercy to those who are on earth, and He Who is in heaven will show mercy to you.'",
            urduText = "حضرت عبداللہ بن عمرو رضی اللہ عنہ سے روایت ہے کہ رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: 'رحم کرنے والوں پر رحمن رحم فرماتا ہے۔ تم زمین والوں پر رحم کرو، آسمان والا تم پر رحم فرمائے گا۔'",
            narrator = "Abdullah ibn Amr (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Al-Albani",
            chapterTitleArabic = "باب فِي الرَّحْمَةِ",
            chapterTitleEnglish = "Regarding Mercy and Compassion",
            chapterTitleUrdu = "رحم اور شفقت کا بیان",
            sourceUrl = "https://sunnah.com/abudawud:4941",
            isBookmarked = false
        ),
        // Tirmidhi Hadith 2516
        HadithEntity(
            id = "tirmidhi_2516",
            collectionName = "tirmidhi",
            bookNumber = "38",
            hadithNumber = "2516",
            inBookReference = "Book 38, Hadith 59",
            arabicText = "عَنِ ابْنِ عَبَّاسٍ، قَالَ كُنْتُ خَلْفَ رَسُولِ اللَّهِ صلى الله عليه وسلم يَوْمًا فَقَالَ:\n\n«يَا غُلاَمُ إِنِّي أُعَلِّمُكَ كَلِمَاتٍ: احْفَظِ اللَّهَ يَحْفَظْكَ، احْفَظِ اللَّهَ تَجِدْهُ تُجَاهَكَ، إِذَا سَأَلْتَ فَاسْأَلِ اللَّهَ، وَإِذَا اسْتَعَنْتَ فَاسْتَعِنْ بِاللَّهِ، وَاعْلَمْ أَنَّ الأُمَّةَ لَوِ اجْتَمَعَتْ عَلَى أَنْ يَنْفَعُوكَ بِشَىْءٍ لَمْ يَنْفَعُوكَ إِلاَّ بِشَىْءٍ قَدْ كَتَبَهُ اللَّهُ لَكَ، وَلَوِ اجْتَمَعُوا عَلَى أَنْ يَضُرُّوكَ بِشَىْءٍ لَمْ يَضُرُّوكَ إِلاَّ بِشَىْءٍ قَدْ كَتَبَهُ اللَّهُ عَلَيْكَ، رُفِعَتِ الأَقْلاَمُ وَجَفَّتِ الصُّحُفُ».",
            englishText = "Ibn Abbas narrated: 'One day I was behind the Messenger of Allah (peace be upon him) and he said: O young man, I will teach you some words: Be mindful of Allah and Allah will protect you. Be mindful of Allah and you will find Him in front of you. If you ask, ask from Allah; and if you seek help, seek help from Allah. And know that if the whole nation were to gather together to benefit you with something, they would only benefit you with what Allah has already written for you. And if they gather to harm you with something, they would only harm you with what Allah has already decreed against you. The pens have been lifted and the pages have dried.'",
            urduText = "حضرت ابن عباس رضی اللہ عنہما نے فرمایا: ایک روز میں رسول اللہ صلی اللہ علیہ وسلم کے پیچھے سواری پر تھا۔ آپ نے فرمایا: 'اے لڑکے! میں تمہیں چند اہم باتیں سکھاتا ہوں: اللہ کے احکام کی حفاظت کرو، اللہ تمہاری حفاظت فرمائے گا۔ اللہ کے حقوق کا خیال رکھو، تم اسے اپنے سامنے پاؤ گے۔ جب مانگنا ہو تو اللہ ہی سے مانگو، اور جب مدد طلب کرنی ہو تو اللہ ہی سے مدد چاہو۔ اور یہ جان لو کہ اگر تمام امت مل کر بھی تمہیں کوئی فائدہ پہنچانا چاہے تو تمہیں اتنا ہی فائدہ پہنچا سکتی ہے جتنا اللہ نے تمہارے مقدر میں لکھ دیا ہے؛ اور اگر سب مل کر تمہیں کوئی نقصان پہنچانا چاہیں تو تمہیں اتنا ہی نقصان پہنچا سکتے ہیں جتنا اللہ نے تمہارے مقدر میں لکھ دیا ہے۔ قلم اٹھا لیے گئے اور صحیفے خشک ہو چکے ہیں۔'",
            narrator = "Abdullah ibn Abbas (رضي الله عنهما)",
            grade = "Hasan Sahih",
            gradedBy = "Abu 'Isa at-Tirmidhi",
            chapterTitleArabic = "باب حِفْظِ اللَّهِ وَالتَّوَكُّلِ عَلَيْهِ",
            chapterTitleEnglish = "Being mindful of Allah and reliance upon Him",
            chapterTitleUrdu = "اللہ کے احکامات کی حفاظت اور توکل",
            sourceUrl = "https://sunnah.com/tirmidhi:2516",
            isBookmarked = true
        ),
        // Ibn Majah Hadith 224
        HadithEntity(
            id = "ibnmajah_224",
            collectionName = "ibnmajah",
            bookNumber = "1",
            hadithNumber = "224",
            inBookReference = "Book 1, Hadith 224",
            arabicText = "حَدَّثَنَا هِشَامُ بْنُ عَمَّارٍ، حَدَّثَنَا حَفْصُ بْنُ سُلَيْمَانَ، حَدَّثَنَا كَثِيرُ بْنُ شِنْظِيرٍ، عَنْ مُحَمَّدِ بْنِ سِيرِينَ، عَنْ أَنَسِ بْنِ مَالِكٍ، قَالَ قَالَ رَسُولُ اللَّهِ صلى الله عليه وسلم:\n\n«طَلَبُ الْعِلْمِ فَرِيضَةٌ عَلَى كُلِّ مُسْلِمٍ، وَوَاضِعُ الْعِلْمِ عِنْدَ غَيْرِ أَهْلِهِ كَمُقَلِّدِ الْخَنَازِيرِ الْجَوْهَرَ وَاللُّؤْلُؤَ وَالذَّهَبَ».",
            englishText = "It was narrated that Anas bin Malik said: 'The Messenger of Allah (peace be upon him) said: Seeking knowledge is a duty upon every Muslim.'",
            urduText = "حضرت انس بن مالک رضی اللہ عنہ سے روایت ہے کہ رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: 'علم حاصل کرنا ہر مسلمان پر فرض ہے۔'",
            narrator = "Anas ibn Malik (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Al-Albani",
            chapterTitleArabic = "باب فَضْلِ الْعُلَمَاءِ وَالْحَثِّ عَلَى طَلَبِ الْعِلْمِ",
            chapterTitleEnglish = "The Virtue of Knowledge and Urging to Seek it",
            chapterTitleUrdu = "علم کی فضیلت اور حصول علم کی ترغیب",
            sourceUrl = "https://sunnah.com/ibnmajah:224",
            isBookmarked = false
        ),
        // Muwatta Malik Book 47 Hadith 8
        HadithEntity(
            id = "malik_8",
            collectionName = "malik",
            bookNumber = "47",
            hadithNumber = "8",
            inBookReference = "Book 47, Hadith 8",
            arabicText = "وَحَدَّثَنِي عَنْ مَالِكٍ، أَنَّهُ قَدْ بَلَغَهُ أَنَّ رَسُولَ اللَّهِ صلى الله عليه وسلم قَالَ:\n\n«بُعِثْتُ لأُتَمِّمَ حُسْنَ الأَخْلاَقِ».",
            englishText = "Yahya related to me from Malik that he had heard that the Messenger of Allah (peace and blessings be upon him) said: 'I was sent only to perfect noble character and good morals.'",
            urduText = "حضرت امام مالک سے روایت ہے کہ انہیں خبر پہنچی کہ رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: 'میں اس لیے بھیجا گیا ہوں تاکہ اچھے اخلاق کو پایہ تکمیل تک پہنچاؤں۔'",
            narrator = "Imam Malik (بلاغ / مرسل صحيح بشواهده)",
            grade = "Sahih",
            gradedBy = "Ibn Abd al-Barr / Al-Albani",
            chapterTitleArabic = "باب مَا جَاءَ فِي حُسْنِ الْخُلُقِ",
            chapterTitleEnglish = "What Has Been Related About Noble Character",
            chapterTitleUrdu = "حسن اخلاق کا بیان",
            sourceUrl = "https://sunnah.com/malik:8",
            isBookmarked = false
        ),
        // Musnad Ahmad Hadith 23489 (The Farewell Pilgrimage Sermon on Human Equality)
        HadithEntity(
            id = "ahmad_23489",
            collectionName = "ahmad",
            bookNumber = "1",
            hadithNumber = "23489",
            inBookReference = "Musnad Ahmad 23489",
            arabicText = "حَدَّثَنَا إِسْمَاعِيلُ، حَدَّثَنَا سَعِيدٌ الْجُرَيْرِيُّ، عَنْ أَبِي نَضْرَةَ، حَدَّثَنِي مَنْ سَمِعَ خُطْبَةَ رَسُولِ اللَّهِ صلى الله عليه وسلم فِي وَسَطِ أَيَّامِ التَّشْرِيقِ فَقَالَ:\n\n«يَا أَيُّهَا النَّاسُ، أَلاَ إِنَّ رَبَّكُمْ وَاحِدٌ، وَإِنَّ أَبَاكُمْ وَاحِدٌ، أَلاَ لاَ فَضْلَ لِعَرَبِيٍّ عَلَى أَعْجَمِيٍّ، وَلاَ لِعَجَمِيٍّ عَلَى عَرَبِيٍّ، وَلاَ لأَحْمَرَ عَلَى أَسْوَدَ، وَلاَ أَسْوَدَ عَلَى أَحْمَرَ إِلاَّ بِالتَّقْوَى، أَبَلَّغْتُ؟ قَالُوا: بَلَّغَ رَسُولُ اللَّهِ صلى الله عليه وسلم».",
            englishText = "Narrated by someone who heard the sermon of Allah's Messenger (peace be upon him) during the days of Tashreeq: 'O people, verily your Lord is One and your father is one. Behold, there is no superiority for an Arab over a non-Arab, nor for a non-Arab over an Arab, nor for a red person over a black person, nor for a black person over a red person except by piety (taqwa). Have I conveyed the message?' They said: 'The Messenger of Allah has conveyed it.'",
            urduText = "ایام تشریق میں رسول اللہ صلی اللہ علیہ وسلم کے خطبے سے روایت ہے کہ آپ نے فرمایا: 'اے لوگو! سن لو کہ تمہارا رب ایک ہے اور تمہارا باپ (آدم) ایک ہے۔ خبردار! کسی عربی کو کسی عجمی پر اور کسی عجمی کو کسی عربی پر، نہ کسی گورے کو کسی کالے پر اور نہ کسی کالے کو کسی گورے پر کوئی فضیلت حاصل ہے سوائے تقویٰ کے۔ کیا میں نے پیغام پہنچا دیا؟ صحابہ نے عرض کیا: رسول اللہ نے پیغام پہنچا دیا۔'",
            narrator = "Sahabi from the Farewell Pilgrimage (رضي الله عنه)",
            grade = "Sahih",
            gradedBy = "Shu'ayb al-Arna'ut / Al-Albani",
            chapterTitleArabic = "خطبة حجة الوداع في المساواة والتقوى",
            chapterTitleEnglish = "The Farewell Sermon: Equality of Mankind and Piety",
            chapterTitleUrdu = "خطبہ حجۃ الوداع: انسانی مساوات اور تقویٰ",
            sourceUrl = "https://sunnah.com/ahmad:23489",
            isBookmarked = true
        )
    )
}
