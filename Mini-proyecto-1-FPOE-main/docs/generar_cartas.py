"""Genera las 52 cartas (+ dorso) como PNG. Diseño propio, sin material con derechos de autor.
Nombre de archivo: <rango>_<palo>.png  (ej. K_corazones.png, 10_picas.png, A_treboles.png)
Uso: python3 gen_cartas.py <carpeta_salida>
"""
import sys
from PIL import Image, ImageDraw, ImageFont

W, H = 240, 336
FUENTE = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
FUENTE_B = "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"

PALOS = {
    "corazones": ("♥", (198, 40, 40)),
    "diamantes": ("♦", (198, 40, 40)),
    "treboles": ("♣", (33, 33, 33)),
    "picas": ("♠", (33, 33, 33)),
}
RANGOS = ["A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"]

# posiciones (x, y) normalizadas de los símbolos centrales
PIPS = {
    "A": [(.5, .5)],
    "2": [(.5, .22), (.5, .78)],
    "3": [(.5, .22), (.5, .5), (.5, .78)],
    "4": [(.3, .22), (.7, .22), (.3, .78), (.7, .78)],
    "5": [(.3, .22), (.7, .22), (.5, .5), (.3, .78), (.7, .78)],
    "6": [(.3, .22), (.7, .22), (.3, .5), (.7, .5), (.3, .78), (.7, .78)],
    "7": [(.25, .2), (.75, .2), (.5, .35), (.25, .5), (.75, .5), (.25, .8), (.75, .8)],
    "8": [(.25, .2), (.75, .2), (.5, .35), (.25, .5), (.75, .5), (.5, .65), (.25, .8), (.75, .8)],
    "9": [(.25, .2), (.75, .2), (.25, .4), (.75, .4), (.5, .5), (.25, .6), (.75, .6), (.25, .8), (.75, .8)],
    "10": [(.25, .2), (.75, .2), (.5, .3), (.25, .4), (.75, .4), (.25, .6), (.75, .6), (.5, .7), (.25, .8), (.75, .8)],
}


def fuente(ruta, tam):
    return ImageFont.truetype(ruta, tam)


def glifo(capa, texto, centro, tam, color, ruta=FUENTE, girar=False):
    """Dibuja `texto` centrado en `centro`; si girar=True lo rota 180 grados."""
    f = fuente(ruta, tam)
    tmp = Image.new("RGBA", (tam * 3, tam * 3), (0, 0, 0, 0))
    d = ImageDraw.Draw(tmp)
    d.text((tam * 1.5, tam * 1.5), texto, font=f, fill=color + (255,), anchor="mm")
    if girar:
        tmp = tmp.rotate(180)
    capa.alpha_composite(tmp, (int(centro[0] - tam * 1.5), int(centro[1] - tam * 1.5)))


def carta(rango, palo):
    simbolo, color = PALOS[palo]
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((1, 1, W - 2, H - 2), radius=18, fill=(255, 255, 255, 255),
                        outline=(120, 120, 120, 255), width=3)

    tam_rango = 40 if rango != "10" else 34
    # índices en esquinas (arriba-izquierda y abajo-derecha girado)
    glifo(img, rango, (30, 34), tam_rango, color, FUENTE_B)
    glifo(img, simbolo, (30, 72), 30, color)
    glifo(img, rango, (W - 30, H - 34), tam_rango, color, FUENTE_B, girar=True)
    glifo(img, simbolo, (W - 30, H - 72), 30, color, girar=True)

    x0, x1, y0, y1 = 62, W - 62, 40, H - 40
    area_w, area_h = x1 - x0, y1 - y0

    if rango in ("J", "Q", "K"):
        # recuadro con la letra grande y el palo en dos extremos
        d.rounded_rectangle((x0 - 8, y0, x1 + 8, y1), radius=10, outline=color + (255,), width=3,
                            fill=(color[0] // 12 + 235, color[1] // 12 + 235, color[2] // 12 + 235, 255))
        glifo(img, rango, (W / 2, H / 2), 96, color, FUENTE_B)
        glifo(img, simbolo, (W / 2, y0 + 34), 44, color)
        glifo(img, simbolo, (W / 2, y1 - 34), 44, color, girar=True)
    else:
        tam = 92 if rango == "A" else (36 if rango in ("7", "8", "9", "10") else 46)
        for (px, py) in PIPS[rango]:
            cx = x0 + px * area_w
            cy = y0 + py * area_h
            glifo(img, simbolo, (cx, cy), tam, color, girar=(py > .5))
    return img


def dorso():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((1, 1, W - 2, H - 2), radius=18, fill=(255, 255, 255, 255),
                        outline=(120, 120, 120, 255), width=3)
    d.rounded_rectangle((14, 14, W - 15, H - 15), radius=10, fill=(30, 64, 140, 255))
    for i in range(-H, W, 24):
        d.line((i, 14, i + H, H - 15), fill=(70, 110, 190, 255), width=3)
        d.line((i + H, 14, i, H - 15), fill=(70, 110, 190, 255), width=3)
    # tapa los bordes del patrón para que no se salga del marco
    marco = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    m = ImageDraw.Draw(marco)
    m.rounded_rectangle((1, 1, W - 2, H - 2), radius=18, outline=(120, 120, 120, 255), width=3)
    m.rounded_rectangle((14, 14, W - 15, H - 15), radius=10, outline=(255, 255, 255, 255), width=5)
    base = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    bd = ImageDraw.Draw(base)
    bd.rounded_rectangle((1, 1, W - 2, H - 2), radius=18, fill=(255, 255, 255, 255))
    mask = Image.new("L", (W, H), 0)
    ImageDraw.Draw(mask).rounded_rectangle((14, 14, W - 15, H - 15), radius=10, fill=255)
    base.paste(img, (0, 0), mask)
    base.alpha_composite(marco)
    glifo(base, "21", (W / 2, H / 2), 64, (255, 255, 255), FUENTE_B)
    return base


def main(salida):
    import os
    os.makedirs(salida, exist_ok=True)
    n = 0
    for palo in PALOS:
        for rango in RANGOS:
            carta(rango, palo).save(os.path.join(salida, f"{rango}_{palo}.png"))
            n += 1
    dorso().save(os.path.join(salida, "dorso.png"))
    print(f"{n} cartas + dorso generados en {salida}")


if __name__ == "__main__":
    main(sys.argv[1])
