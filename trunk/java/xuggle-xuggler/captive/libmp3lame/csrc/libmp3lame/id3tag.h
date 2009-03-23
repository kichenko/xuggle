
#ifndef LAME_ID3_H
#define LAME_ID3_H


#define CHANGED_FLAG    (1U << 0)
#define ADD_V2_FLAG     (1U << 1)
#define V1_ONLY_FLAG    (1U << 2)
#define V2_ONLY_FLAG    (1U << 3)
#define SPACE_V1_FLAG   (1U << 4)
#define PAD_V2_FLAG     (1U << 5)

enum {
    MIMETYPE_NONE = 0,
    MIMETYPE_JPEG,
    MIMETYPE_PNG,
    MIMETYPE_GIF,
};

typedef struct FrameDataNode
{
    struct FrameDataNode* nxt;
    int     fid;                /* Frame Identifier                 */
    char    lng[4];             /* 3-character language descriptor  */
    struct {
        union {
            char*           l;  /* ptr to Latin-1 chars             */
            unsigned short* u;  /* ptr to UCS-2 text                */
            unsigned char*  b;  /* ptr to raw bytes                 */
        }       ptr;
        size_t  dim;
        int     enc;            /* 0:Latin-1, 1:UCS-2, 2:RAW        */
    } dsc, txt;
} FrameDataNode;


typedef struct id3tag_spec {
    /* private data members */
    int     flags;
    int     year;
    char   *title;
    char   *artist;
    char   *album;
    char   *comment;
    int     track_id3v1;
    int     genre_id3v1;
    unsigned char *albumart;
    int     albumart_size;
    int     albumart_mimetype;
    char  **values;
    unsigned int num_values;
    FrameDataNode* v2_head, *v2_tail;
} id3tag_spec;


/* write tag into stream at current position */
extern int id3tag_write_v2(lame_global_flags * gfp);
extern int id3tag_write_v1(lame_global_flags * gfp);
/*
 * NOTE: A version 2 tag will NOT be added unless one of the text fields won't
 * fit in a version 1 tag (e.g. the title string is longer than 30 characters),
 * or the "id3tag_add_v2" or "id3tag_v2_only" functions are used.
 */
/* experimental */
int CDECL id3tag_set_textinfo_latin1(
        lame_global_flags* gfp,
        char const*        id,
        char const*        text );

/* experimental */
int CDECL id3tag_set_textinfo_ucs2(
        lame_global_flags*    gfp, 
        char const*           id,
        unsigned short const* text );

/* experimental */
int CDECL id3tag_set_comment_latin1(
        lame_global_flags* gfp,
        char const*        lang,
        char const*        desc,
        char const*        text );

/* experimental */
int CDECL id3tag_set_comment_ucs2(
        lame_global_flags*    gfp, 
        char const*           lang,
        unsigned short const* desc,
        unsigned short const* text );

#endif